package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.error.{ InterpTimeout, NotSupported }
import kr.ac.kaist.jiset.js.ast.Lexical
import kr.ac.kaist.jiset.js.{ Parser => ESParser, _ }
import kr.ac.kaist.jiset.parser.ESValueParser
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.{ TEST_MODE, STAT, DEBUG, TIMEOUT }
import scala.annotation.tailrec
import scala.collection.mutable.{ Map => MMap }

// IR Interpreter
class Interp(
  st: State,
  timeLimit: Option[Long] = Some(TIMEOUT),
  useHook: Boolean = false
) {
  import Interp._

  // cursor generator
  val cursorGen: CursorGen[_ <: Cursor] = st.cursorGen

  // set start time of interpreter
  val startTime: Long = System.currentTimeMillis

  // the number of instructions
  def getIter: Int = iter
  private var iter: Int = 0

  // iteration period for check
  val CHECK_PERIOD = 10000

  // step target
  trait StepTarget {
    override def toString: String = this match {
      case Terminate => "TERMINATED"
      case ReturnUndef => "RETURN"
      case NextStep(cursor) => cursor.beautified(detail = false)
    }
  }
  case object Terminate extends StepTarget
  case object ReturnUndef extends StepTarget
  case class NextStep(cursor: Cursor) extends StepTarget

  // get next step target
  def nextTarget: StepTarget = st.context.cursorOpt match {
    case Some(cursor) => NextStep(cursor)
    case None => st.ctxtStack match {
      case Nil => Terminate
      case _ => ReturnUndef
    }
  }

  // step
  final def step: Boolean = nextTarget match {
    case Terminate =>
      // stop evaluation
      if (STAT) Stat.recordIter(st.fnameOpt, iter)
      false
    case ReturnUndef =>
      // do return
      doReturn(Undef)

      // keep going
      true
    case NextStep(cursor) => {
      // print statistics
      iter += 1
      if (iter % CHECK_PERIOD == 0) timeLimit.map(limit => {
        val duration = (System.currentTimeMillis - startTime) / 1000
        if (duration > limit) throw InterpTimeout
      })

      // text-based debugging
      if (DEBUG) cursor match {
        case InstCursor(ISeq(_), _) =>
        case _ =>
          println(s"${st.context.name}: ${cursor.beautified(detail = false)}")
      }

      // interp the current cursor
      catchReturn(cursor match {
        case cursor @ InstCursor(inst, rest) =>
          interp(inst, rest)
        case NodeCursor(node) =>
          interp(node)
      })

      // garbage collection
      if (iter % 100000 == 0) GC(st, storedValues)

      // keep going
      true
    }
  }

  // fixpoint
  @tailrec
  final def fixpoint: State = step match {
    case true => fixpoint
    case false => st
  }

  // transition for nodes
  def interp(node: Node): Unit = node match {
    case Entry(_) => st.moveNext
    case Normal(_, inst) => interp(inst)
    case Call(_, inst) => interp(inst)
    case Arrow(_, inst, fid) => interp(inst)
    case branch @ Branch(_, inst) => {
      val (thenNode, elseNode) = cfg.branchOf(branch)
      st.context.cursorOpt = Some(interp(inst.cond).escaped(st) match {
        case Bool(true) => NodeCursor(thenNode)
        case Bool(false) => NodeCursor(elseNode)
        case v => error(s"not a boolean: ${v.beautified}")
      })
    }
    case Exit(_) => st.context.cursorOpt = None
  }

  // transition for instructions
  def interp(inst: Inst, rest: List[Inst]): Unit = inst match {
    case inst: CondInst => interp(inst, rest)
    case inst: CallInst => interp(inst)
    case inst: NormalInst => interp(inst)
    case inst: ArrowInst => interp(inst)
    case inst: ISeq => interp(inst, rest)
  }

  // transition for conditional instructions
  def interp(inst: CondInst, rest: List[Inst]): Unit = {
    st.context.cursorOpt = inst match {
      case IIf(cond, thenInst, elseInst) => interp(cond).escaped(st) match {
        case Bool(true) => Some(InstCursor(thenInst, rest))
        case Bool(false) => Some(InstCursor(elseInst, rest))
        case v => error(s"not a boolean: ${v.beautified}")
      }
      case IWhile(cond, body) => interp(cond).escaped(st) match {
        case Bool(true) => Some(InstCursor(body, inst :: rest))
        case Bool(false) => InstCursor.from(rest)
        case v => error(s"not a boolean: ${v.beautified}")
      }
    }
  }

  // transition for call instructions
  def interp(inst: CallInst): Unit = {
    st.moveNext
    inst match {
      case IApp(id, ERef(RefId(Id(name))), args) if simpleFuncs contains name => {
        val vs =
          if (name == "IsAbruptCompletion") args.map(interp)
          else args.map(interp(_).escaped(st))
        st.context.locals += id -> simpleFuncs(name)(st, vs)
      }
      case IApp(id, fexpr, args) => interp(fexpr) match {
        case Func(algo) => {
          val head = algo.head
          val body = algo.body
          val vs = args.map(interp)
          val locals = getLocals(head.params, vs)
          val cursorOpt = cursorGen(body)
          val context = Context(cursorOpt, id, head.name, None, Some(algo), locals)
          if (STAT) Stat.touchAlgo(algo.name)
          st.ctxtStack ::= st.context
          st.context = context

          // use hooks
          if (useHook) notify(Event.Call)
        }
        case Clo(ctxtName, params, locals, cursorOpt) => {
          val vs = args.map(interp)
          val newLocals = locals ++ getLocals(params.map(x => Param(x.name)), vs)
          val context = Context(cursorOpt, id, ctxtName + ":closure", None, None, locals)
          st.ctxtStack ::= st.context
          st.context = context

          // use hooks
          if (useHook) notify(Event.Call)
        }
        case Cont(params, context, ctxtStack) => {
          val vs = args.map(interp)
          st.context = context.copied
          st.context.locals ++= params zip vs
          st.ctxtStack = ctxtStack.map(_.copied)

          // use hooks
          if (useHook) notify(Event.Cont)
        }
        case v => error(s"not a function: ${fexpr.beautified} -> ${v.beautified}")
      }
      case IAccess(id, bexpr, expr, args) => {
        var base = interp(bexpr)
        val prop = interp(expr).escaped(st)
        prop match {
          case Str("Type" | "Value" | "Target") =>
          case _ => base = base.escaped(st)
        }
        val vOpt = (base, prop) match {
          case (ASTVal(Lexical(kind, str)), Str(name)) => Some((kind, name) match {
            case ("(IdentifierName \\ (ReservedWord))" | "IdentifierName", "StringValue") => Str(str)
            case ("NumericLiteral", "MV" | "NumericValue") => ESValueParser.parseNumber(str)
            case ("StringLiteral", "SV" | "StringValue") => Str(ESValueParser.parseString(str))
            case ("NoSubstitutionTemplate", "TV") => Str(ESValueParser.parseTVNoSubstitutionTemplate(str))
            case ("TemplateHead", "TV") => Str(ESValueParser.parseTVTemplateHead(str))
            case ("TemplateMiddle", "TV") => Str(ESValueParser.parseTVTemplateMiddle(str))
            case ("TemplateTail", "TV") => Str(ESValueParser.parseTVTemplateTail(str))
            case ("NoSubstitutionTemplate", "TRV") => Str(ESValueParser.parseTRVNoSubstitutionTemplate(str))
            case ("TemplateHead", "TRV") => Str(ESValueParser.parseTRVTemplateHead(str))
            case ("TemplateMiddle", "TRV") => Str(ESValueParser.parseTRVTemplateMiddle(str))
            case ("TemplateTail", "TRV") => Str(ESValueParser.parseTRVTemplateTail(str))
            case (_, "Contains") => Bool(false)
            case ("RegularExpressionLiteral", name) => throw NotSupported(s"RegularExpressionLiteral.$name")
            case _ => error(s"invalid Lexical access: $kind.$name")
          })
          case (ASTVal(ast), Str("parent")) => Some(ast.parent.map(ASTVal).getOrElse(Absent))
          case (ASTVal(ast), Str("children")) => Some(st.allocList(ast.children))
          case (ASTVal(ast), Str("kind")) => Some(Str(ast.kind))
          case (ASTVal(ast), Str(name)) => ast.staticMap.get(name) match {
            case Some(value) => Some((value match {
              case addr: DynamicAddr => st.copyObj(addr)
              case _ => value
            }).wrapCompletion(st))
            case None => ast.semantics(name) match {
              case Some((algo, asts)) => {
                val head = algo.head
                val body = algo.body
                val vs = asts ++ args.map(interp)
                val locals = getLocals(head.params, vs)
                val cursorOpt = cursorGen(body)
                val context = Context(cursorOpt, id, head.name, Some(ast), Some(algo), locals)
                if (STAT) Stat.touchAlgo(algo.name)
                st.ctxtStack ::= st.context
                st.context = context

                // use hooks
                if (useHook) notify(Event.Call)
                None
              }
              case None => Some(ast.subs(name).getOrElse {
                error(s"unexpected semantics: ${ast.name}.$name")
              })
            }
          }
          case (addr: Addr, _) => Some(st(addr, prop))
          case (Str(str), _) => Some(st(str, prop))
          case v => error(s"invalid access: ${inst.beautified}")
        }
        vOpt.map(st.context.locals += id -> _)
      }
    }
  }

  // transition for normal instructions
  def interp(inst: NormalInst): Unit = {
    st.moveNext
    inst match {
      case IExpr(expr) => interp(expr)
      case ILet(id, expr) => st.context.locals += id -> interp(expr)
      case IAssign(ref, expr) => st.update(interp(ref), interp(expr))
      case IDelete(ref) => st.delete(interp(ref))
      case IAppend(expr, list) => interp(list).escaped(st) match {
        case (addr: Addr) => st.append(addr, interp(expr).escaped(st))
        case v => error(s"not an address: ${v.beautified}")
      }
      case IPrepend(expr, list) => interp(list).escaped(st) match {
        case (addr: Addr) => st.prepend(addr, interp(expr).escaped(st))
        case v => error(s"not an address: ${v.beautified}")
      }
      case IReturn(expr) => throw ReturnValue(interp(expr))
      case IThrow(name) => {
        val addr = st.allocMap(Ty("OrdinaryObject"), Map(
          Str("Prototype") -> NamedAddr(s"GLOBAL.$name.prototype"),
          Str("ErrorData") -> Undef
        ))
        throw ReturnValue(addr.wrapCompletion(st, CompletionType.Throw))
      }
      case IAssert(expr) => interp(expr).escaped(st) match {
        case Bool(true) =>
        case v => error(s"assertion failure: ${expr.beautified}")
      }
      case IPrint(expr) => {
        val v = interp(expr)
        if (!TEST_MODE) println(st.getString(v))
      }
    }
  }

  // transition for arrow instructions
  def interp(inst: ArrowInst): Unit = {
    st.moveNext
    inst match {
      case IClo(id, params, captured, body) => st.context.locals += id -> Clo(
        st.context.name,
        params,
        MMap.from(captured.map(x => x -> st(x))),
        cursorGen(body),
      )
      case ICont(id, params, body) => {
        val newCtxt = st.context.copied
        newCtxt.cursorOpt = cursorGen(body)
        val newCtxtStack = st.ctxtStack.map(_.copied)
        st.context.locals += id -> Cont(
          params,
          newCtxt,
          newCtxtStack,
        )
      }
      case IWithCont(id, params, body) => {
        val State(_, context, ctxtStack, _, _, _) = st
        st.context = context.copied
        st.context.cursorOpt = cursorGen(body)
        st.context.locals += id -> Cont(params, context, ctxtStack)
        st.ctxtStack = ctxtStack.map(_.copied)
      }
    }
  }

  // transition for sequence instructions
  def interp(inst: ISeq, rest: List[Inst]): Unit =
    st.context.cursorOpt = InstCursor.from(inst.insts ++ rest)

  // catch return values
  def catchReturn(f: => Unit): Unit =
    try f catch { case ReturnValue(value) => doReturn(value) }

  // return value
  private case class ReturnValue(value: Value) extends Throwable

  // store abstract semantics results
  var storedValues: Set[Value] = Set()
  def storeStatic(value: Value): Unit = for {
    ast <- st.context.astOpt
    algo <- st.context.algo
    name <- algo.head match {
      case h: SyntaxDirectedHead if h.isStatic && h.withParams.isEmpty =>
        Some(h.methodName)
      case _ => None
    }
  } {
    val rawValue = (
      if (value.isAbruptCompletion(st)) value
      else value.escaped(st)
    ) match {
        case addr: DynamicAddr => st.copyObj(addr)
        case value => value
      }
    ast.staticMap += name -> rawValue
    storedValues += rawValue
  }

  // return helper
  def doReturn(value: Value): Unit = st.ctxtStack match {
    case Nil =>
      st.context.locals += Id(RESULT) -> value.wrapCompletion(st)
      st.context.cursorOpt = None
    case ctxt :: rest => {
      // proper type handle
      (value, setTypeMap.get(st.context.name)) match {
        case (addr: Addr, Some(ty)) if !addr.isCompletion(st) =>
          st.setType(addr, ty)
        case _ =>
      }

      // store abstract semantics results
      storeStatic(value)

      // return wrapped values
      ctxt.locals += st.context.retId -> value.wrapCompletion(st)
      st.context = ctxt
      st.ctxtStack = rest

      // use hooks
      if (useHook) notify(Event.Return)
    }
  }

  // expresssions
  def interp(expr: Expr): Value = expr match {
    case ENum(n) => Num(n)
    case EINum(n) => INum(n)
    case EBigINum(b) => BigINum(b)
    case EStr(str) => Str(str)
    case EBool(b) => Bool(b)
    case EUndef => Undef
    case ENull => Null
    case EAbsent => Absent
    case EMap(ty, props) => {
      val addr = st.allocMap(ty)
      for ((kexpr, vexpr) <- props) {
        val k = interp(kexpr).escaped(st)
        val v = interp(vexpr)
        st.update(addr, k, v)
      }
      addr
    }
    case EList(exprs) => st.allocList(exprs.map(expr => interp(expr).escaped(st)))
    case ESymbol(desc) => interp(desc) match {
      case (str: Str) => st.allocSymbol(str)
      case Undef => st.allocSymbol(Undef)
      case v => error(s"not a string: ${v.beautified}")
    }
    case EPop(list, idx) => interp(list).escaped(st) match {
      case (addr: Addr) => st.pop(addr, interp(idx).escaped(st))
      case v => error(s"not an address: ${v.beautified}")
    }
    case ERef(ref) => st(interp(ref))
    case EUOp(uop, expr) => {
      val x = interp(expr).escaped(st)
      interp(uop, x)
    }
    case EBOp(OAnd, left, right) => shortCircuit(OAnd, left, right)
    case EBOp(OOr, left, right) => shortCircuit(OOr, left, right)
    case EBOp(OEq, ERef(RefId(id)), EAbsent) => {
      val defined = st.globals.contains(id) || st.locals.contains(id)
      Bool(!defined || st.directLookup(id) == Absent)
    }
    case EBOp(bop, left, right) => {
      val l = interp(left).escaped(st)
      val r = interp(right).escaped(st)
      interp(bop, l, r)
    }
    case ETypeOf(expr) => Str(interp(expr).escaped(st) match {
      case (addr: Addr) => st(addr).ty.name match {
        case name if name endsWith "Object" => "Object"
        case name => name
      }
      case Num(_) | INum(_) => "Number"
      case BigINum(_) => "BigInt"
      case Str(_) => "String"
      case Bool(_) => "Boolean"
      case Undef => "Undefined"
      case Null => "Null"
      case Absent => "Absent"
      case Func(_) => "Function"
      case Clo(_, _, _, _) => "Closure"
      case Cont(_, _, _) => "Continuation"
      case ASTVal(_) => "AST"
    })
    case EIsCompletion(expr) => Bool(interp(expr).isCompletion(st))
    case EIsInstanceOf(base, name) => {
      val bv = interp(base)
      if (bv.isAbruptCompletion(st)) Bool(false)
      else bv.escaped(st) match {
        case ASTVal(ast) => Bool(ast.name == name || ast.getKinds.contains(name))
        case Str(str) => Bool(str == name)
        case addr: Addr => st(addr) match {
          case IRMap(ty, _, _) => Bool(ty < Ty(name))
          case _ => Bool(false)
        }
        case _ => Bool(false)
      }
    }
    case EGetElems(base, name) => interp(base).escaped(st) match {
      case ASTVal(ast) => st.allocList(ast.getElems(name).map(ASTVal(_)))
      case v => error(s"not an AST value: ${v.beautified}")
    }
    case EGetSyntax(base) => interp(base).escaped(st) match {
      case ASTVal(ast) => Str(ast.toString)
      case v => error(s"not an AST value: ${v.beautified}")
    }
    case EParseSyntax(code, rule, parserParams) => {
      val v = interp(code).escaped(st)
      val p = interp(rule).escaped(st) match {
        case Str(str) => ESParser.rules.getOrElse(str, error(s"not exist parse rule: $rule"))
        case v => error(s"not a string: $v")
      }
      v match {
        case ASTVal(ast) => ASTVal(ESParser.parse(p(ast.parserParams), ast.toString).get.checkSupported)
        case Str(str) =>
          val ps = interp(parserParams).escaped(st) match {
            case addr: Addr => st(addr) match {
              case IRList(vs) => vs.toList.map(_ match {
                case Bool(b) => b
                case v => error(s"non-boolean parser parameter: ${v.beautified}")
              })
              case obj => error(s"not a list: ${obj.beautified}")
            }
            case v => error(s"not an address: ${v.beautified}")
          }
          ASTVal(ESParser.parse(p(ps), str).get.checkSupported)
        case v => error(s"not an AST value or a string: $v")
      }
    }
    case EConvert(source, target, flags) => interp(source).escaped(st) match {
      case Str(s) => target match {
        case CStrToNum => Num(ESValueParser.str2num(s))
        case CStrToBigInt => ESValueParser.str2bigint(s)
        case _ => error(s"not convertable option: Str to ${target.beautified}")
      }
      case INum(n) => {
        val radix = flags match {
          case e :: rest => interp(e).escaped(st) match {
            case INum(n) => n.toInt
            case Num(n) => n.toInt
            case _ => error("radix is not int")
          }
          case _ => 10
        }
        target match {
          case CNumToStr => Str(toStringHelper(n, radix))
          case CNumToInt => INum(n)
          case CNumToBigInt => BigINum(BigInt(n))
          case _ => error(s"not convertable option: INum to $target")
        }
      }
      case Num(n) => {
        val radix = flags match {
          case e :: rest => interp(e).escaped(st) match {
            case INum(n) => n.toInt
            case Num(n) => n.toInt
            case _ => error("radix is not int")
          }
          case _ => 10
        }
        target match {
          case CNumToStr => Str(toStringHelper(n, radix))
          case CNumToInt => INum((math.signum(n) * math.floor(math.abs(n))).toLong)
          case CNumToBigInt => BigINum(BigInt(new java.math.BigDecimal(n).toBigInteger))
          case _ => error(s"not convertable option: INum to $target")
        }
      }
      case BigINum(b) => target match {
        case CNumToBigInt => BigINum(b)
        case CNumToStr => Str(b.toString)
        case CBigIntToNum => Num(b.toDouble)
        case _ => error(s"not convertable option: BigINum to $target")
      }
      case v => error(s"not an convertable value: ${v.beautified}")
    }
    case EContains(list, elem) => interp(list).escaped(st) match {
      case addr: Addr => st(addr) match {
        case IRList(vs) => Bool(vs contains interp(elem).escaped(st))
        case obj => error(s"not a list: ${obj.beautified}")
      }
      case v => error(s"not an address: ${v.beautified}")
    }
    case EReturnIfAbrupt(rexpr @ ERef(ref), check) => {
      val refV = interp(ref)
      val value = returnIfAbrupt(st(refV), check)
      st.update(refV, value)
      value
    }
    case EReturnIfAbrupt(expr, check) => returnIfAbrupt(interp(expr), check)
    case ECopy(obj) => interp(obj).escaped(st) match {
      case addr: Addr => st.copyObj(addr)
      case v => error(s"not an address: ${v.beautified}")
    }
    case EKeys(mobj, intSorted) => interp(mobj).escaped(st) match {
      case addr: Addr => st.keys(addr, intSorted)
      case v => error(s"not an address: ${v.beautified}")
    }
    case ENotSupported(msg) => throw NotSupported(msg)
  }

  // return if abrupt completion
  def returnIfAbrupt(value: Value, check: Boolean): Value = value match {
    case addr: Addr => st(addr) match {
      case obj @ IRMap(Ty("Completion"), _, _) => obj(Str("Type")) match {
        case NamedAddr("CONST_normal") => obj(Str("Value"))
        case _ => {
          if (check) throw ReturnValue(addr)
          else error("unchecked abrupt completion")
        }
      }
      case _ => value
    }
    case _ => value
  }

  // references
  def interp(ref: Ref): RefValue = ref match {
    case RefId(id) => RefValueId(id)
    case RefProp(ref, expr) => {
      var base = st(interp(ref))
      val p = interp(expr).escaped(st)
      p match {
        case Str("Type" | "Value" | "Target") =>
        case _ => base = base.escaped(st)
      }
      base match {
        case (addr: Addr) => RefValueProp(addr, p)
        case Str(str) => RefValueString(str, p)
        case v => error(s"not an address: ${v.beautified}")
      }
    }
  }

  // unary operators
  def interp(uop: UOp, operand: Value): Value = (uop, operand) match {
    case (ONeg, Num(n)) => Num(-n)
    case (ONeg, INum(n)) => INum(-n)
    case (ONeg, BigINum(b)) => BigINum(-b)
    case (ONot, Bool(b)) => Bool(!b)
    case (OBNot, Num(n)) => INum(~(n.toInt))
    case (OBNot, INum(n)) => INum(~n)
    case (OBNot, BigINum(b)) => BigINum(~b)
    case (_, value) => error(s"wrong type of value for the operator ${uop.beautified}: ${value.beautified}")
  }

  // binary operators
  def interp(bop: BOp, left: Value, right: Value): Value = (bop, left, right) match {
    // double operations
    case (OPlus, Num(l), Num(r)) => Num(l + r)
    case (OSub, Num(l), Num(r)) => Num(l - r)
    case (OMul, Num(l), Num(r)) => Num(l * r)
    case (OPow, Num(l), Num(r)) => Num(math.pow(l, r))
    case (ODiv, Num(l), Num(r)) => Num(l / r)
    case (OMod, Num(l), Num(r)) => Num(modulo(l, r))
    case (OUMod, Num(l), Num(r)) => Num(unsigned_modulo(l, r))
    case (OLt, Num(l), Num(r)) => Bool(l < r)

    // double with long operations
    case (OPlus, INum(l), Num(r)) => Num(l + r)
    case (OSub, INum(l), Num(r)) => Num(l - r)
    case (OMul, INum(l), Num(r)) => Num(l * r)
    case (ODiv, INum(l), Num(r)) => Num(l / r)
    case (OMod, INum(l), Num(r)) => Num(modulo(l, r))
    case (OPow, INum(l), Num(r)) => Num(scala.math.pow(l, r))
    case (OUMod, INum(l), Num(r)) => Num(unsigned_modulo(l, r))
    case (OLt, INum(l), Num(r)) => Bool(l < r)
    case (OPlus, Num(l), INum(r)) => Num(l + r)
    case (OSub, Num(l), INum(r)) => Num(l - r)
    case (OMul, Num(l), INum(r)) => Num(l * r)
    case (ODiv, Num(l), INum(r)) => Num(l / r)
    case (OMod, Num(l), INum(r)) => Num(modulo(l, r))
    case (OPow, Num(l), INum(r)) => Num(math.pow(l, r))
    case (OUMod, Num(l), INum(r)) => Num(unsigned_modulo(l, r))
    case (OLt, Num(l), INum(r)) => Bool(l < r)

    // string operations
    case (OPlus, Str(l), Str(r)) => Str(l + r)
    case (OSub, Str(l), INum(r)) => Str(l.dropRight(r.toInt))
    case (OLt, Str(l), Str(r)) => Bool(l < r)

    // long operations
    case (OPlus, INum(l), INum(r)) => INum(l + r)
    case (OSub, INum(l), INum(r)) => INum(l - r)
    case (OMul, INum(l), INum(r)) => INum(l * r)
    case (ODiv, INum(l), INum(r)) => Num(l / r)
    case (OUMod, INum(l), INum(r)) => INum(unsigned_modulo(l, r).toLong)
    case (OMod, INum(l), INum(r)) => INum(modulo(l, r).toLong)
    case (OPow, INum(l), INum(r)) => number(math.pow(l, r))
    case (OLt, INum(l), INum(r)) => Bool(l < r)
    case (OBAnd, INum(l), INum(r)) => INum(l & r)
    case (OBOr, INum(l), INum(r)) => INum(l | r)
    case (OBXOr, INum(l), INum(r)) => INum(l ^ r)
    case (OLShift, INum(l), INum(r)) => INum((l.toInt << r.toInt).toLong)
    case (OSRShift, INum(l), INum(r)) => INum((l.toInt >> r.toInt).toLong)
    case (OURShift, INum(l), INum(r)) => INum(((l.toInt >>> r.toInt) & 0xffffffff).toLong)

    // logical operations
    case (OAnd, Bool(l), Bool(r)) => Bool(l && r)
    case (OOr, Bool(l), Bool(r)) => Bool(l || r)
    case (OXor, Bool(l), Bool(r)) => Bool(l ^ r)

    // equality operations
    case (OEq, INum(l), Num(r)) => Bool(!(r equals -0.0) && l == r)
    case (OEq, Num(l), INum(r)) => Bool(!(l equals -0.0) && l == r)
    case (OEq, Num(l), Num(r)) => Bool(l equals r)
    case (OEq, Num(l), BigINum(r)) => Bool(l == r)
    case (OEq, BigINum(l), Num(r)) => Bool(l == r)
    case (OEq, INum(l), BigINum(r)) => Bool(l == r)
    case (OEq, BigINum(l), INum(r)) => Bool(l == r)
    case (OEq, l, r) => Bool(l == r)

    // double equality operations
    case (OEqual, INum(l), Num(r)) => Bool(l == r)
    case (OEqual, Num(l), INum(r)) => Bool(l == r)
    case (OEqual, Num(l), Num(r)) => Bool(l == r)
    case (OEqual, l, r) => Bool(l == r)

    // double with big integers
    case (OLt, BigINum(l), Num(r)) =>
      Bool(new java.math.BigDecimal(l.bigInteger).compareTo(new java.math.BigDecimal(r)) < 0)
    case (OLt, BigINum(l), INum(r)) =>
      Bool(new java.math.BigDecimal(l.bigInteger).compareTo(new java.math.BigDecimal(r)) < 0)
    case (OLt, Num(l), BigINum(r)) =>
      Bool(new java.math.BigDecimal(l).compareTo(new java.math.BigDecimal(r.bigInteger)) < 0)
    case (OLt, INum(l), BigINum(r)) =>
      Bool(new java.math.BigDecimal(l).compareTo(new java.math.BigDecimal(r.bigInteger)) < 0)

    // big integers
    case (OPlus, BigINum(l), BigINum(r)) => BigINum(l + r)
    case (OLShift, BigINum(l), BigINum(r)) => BigINum(l << r.toInt)
    case (OSRShift, BigINum(l), BigINum(r)) => BigINum(l >> r.toInt)
    case (OSub, BigINum(l), BigINum(r)) => BigINum(l - r)
    case (OSub, BigINum(l), INum(r)) => BigINum(l - r)
    case (OMul, BigINum(l), BigINum(r)) => BigINum(l * r)
    case (ODiv, BigINum(l), BigINum(r)) => BigINum(l / r)
    case (OMod, BigINum(l), BigINum(r)) => BigINum(modulo(l, r))
    case (OUMod, BigINum(l), BigINum(r)) => BigINum(unsigned_modulo(l, r))
    case (OUMod, BigINum(l), INum(r)) => BigINum(unsigned_modulo(l, r))
    case (OLt, BigINum(l), BigINum(r)) => Bool(l < r)
    case (OBAnd, BigINum(l), BigINum(r)) => BigINum(l & r)
    case (OBOr, BigINum(l), BigINum(r)) => BigINum(l | r)
    case (OBXOr, BigINum(l), BigINum(r)) => BigINum(l ^ r)
    case (OPow, BigINum(l), BigINum(r)) => BigINum(l.pow(r.toInt))
    case (OPow, BigINum(l), INum(r)) => BigINum(l.pow(r.toInt))
    case (OPow, BigINum(l), Num(r)) =>
      if (r.toInt < 0) Num(math.pow(l.toDouble, r)) else BigINum(l.pow(r.toInt))

    case (_, lval, rval) => error(s"wrong type: ${lval.beautified} ${bop.beautified} ${rval.beautified}")
  }

  // short circuit evaluation
  def shortCircuit(bop: BOp, left: Expr, right: Expr): Value = {
    val l = interp(left).escaped(st)
    (bop, l) match {
      case (OAnd, Bool(false)) => Bool(false)
      case (OOr, Bool(true)) => Bool(true)
      case _ => {
        val r = interp(right).escaped(st)
        interp(bop, l, r)
      }
    }
  }

  // get initial local variables
  def getLocals(params: List[Param], args: List[Value]): MMap[Id, Value] = {
    val map = MMap[Id, Value]()
    @tailrec
    def aux(ps: List[Param], as: List[Value]): Unit = (ps, as) match {
      case (Nil, Nil) =>
      case (Param(name, kind) :: pl, Nil) => kind match {
        case Param.Kind.Normal => error(s"remaining parameter: $name")
        case _ => {
          map += Id(name) -> Absent
          aux(pl, Nil)
        }
      }
      case (Nil, args) => {
        val argsStr = args.map(_.beautified).mkString("[", ", ", "]")
        error(s"remaining arguments: $argsStr")
      }
      case (param :: pl, arg :: al) => {
        map += Id(param.name) -> arg
        aux(pl, al)
      }
    }
    aux(params, args)
    map
  }

  // hooks
  private var hooks: Set[InterpHook] = Set()
  def subscribe(name: String, kind: Event, f: State => Unit): InterpHook = {
    val hook = InterpHook(name, kind, f)
    hooks += hook
    hook
  }
  def notify(event: Event): Unit = hooks.foreach {
    case InterpHook(_, kind, f) if kind == event => f(st)
    case _ =>
  }
  def unsubscribe(hook: InterpHook): Unit = hooks -= hook
}

// interp hook
case class InterpHook(
  name: String,
  kind: Interp.Event,
  f: State => Unit
)

// interp object
object Interp {
  def apply(
    st: State,
    timeLimit: Option[Long] = Some(TIMEOUT)
  ): State = {
    val interp = new Interp(st, timeLimit)
    interp.fixpoint
    st
  }

  // type update algorithms
  val setTypeMap: Map[String, Ty] = Map(
    "OrdinaryFunctionCreate" -> Ty("ECMAScriptFunctionObject"),
    "ArrayCreate" -> Ty("ArrayExoticObject"),
  )

  // simple functions
  type SimpleFunc = PartialFunction[(State, List[Value]), Value]
  def arityCheck(pair: (String, SimpleFunc)): (String, SimpleFunc) = {
    val (name, f) = pair
    name -> {
      case (st, args) => optional(f(st, args)).getOrElse {
        error(s"wrong arguments: $name(${args.map(_.beautified).mkString(", ")})")
      }
    }
  }
  def numericSimpleFunc(op: (MathValue, MathValue) => MathValue): SimpleFunc = {
    case (st, list @ _ :: _) => {
      val ds = list.collect { case x: Numeric => x.toMathValue }
      val d = ds.reduce(op)
      d.toLong.map(INum).getOrElse {
        d.toBigInt.map(BigINum).getOrElse {
          Num(d.toDouble)
        }
      }
    }
  }
  val simpleFuncs: Map[String, SimpleFunc] = Map(
    arityCheck("GetArgument" -> {
      case (st, List(addr: Addr)) => st(addr) match {
        case list @ IRList(vs) => if (vs.isEmpty) Absent else list.pop(INum(0))
        case _ => error(s"non-list @ GetArgument: ${addr.beautified}")
      }
    }),
    arityCheck("IsDuplicate" -> {
      case (st, List(addr: Addr)) => st(addr) match {
        case IRList(vs) => Bool(vs.toSet.size != vs.length)
        case _ => error(s"non-list @ IsDuplicate: ${addr.beautified}")
      }
    }),
    arityCheck("IsArrayIndex" -> {
      case (st, List(Str(s))) =>
        val d = ESValueParser.str2num(s)
        val ds = toStringHelper(d)
        val UPPER = (1L << 32) - 1
        val l = d.toLong
        Bool(ds == s && 0 <= l && d == l && l < UPPER)
      case (st, List(v)) => Bool(false)
    }),
    arityCheck("min" -> numericSimpleFunc(_ min _)),
    arityCheck("max" -> numericSimpleFunc(_ max _)),
    arityCheck("abs" -> {
      case (st, List(Num(n))) => Num(n.abs)
      case (st, List(INum(n))) => INum(n.abs)
      case (st, List(BigINum(n))) => BigINum(n.abs)
    }),
    arityCheck("floor" -> {
      case (st, List(Num(n))) => INum(n.floor.toLong)
      case (st, List(INum(n))) => INum(n)
      case (st, List(BigINum(n))) => BigINum(n)
    }),
    arityCheck("fround" -> {
      case (st, List(Num(n))) => Num(n.toFloat.toDouble)
      case (st, List(INum(n))) => Num(n.toFloat.toDouble)
    }),
    arityCheck("ThrowCompletion" -> {
      case (st, List(value)) => value.wrapCompletion(st, CompletionType.Throw)
    }),
    arityCheck("NormalCompletion" -> {
      case (st, List(value)) => value.wrapCompletion(st)
    }),
    arityCheck("IsAbruptCompletion" -> {
      case (st, List(value)) => Bool(value.isAbruptCompletion(st))
    }),
  )

  // interp event
  val hidGen = new UIdGen
  type Event = Event.Value
  object Event extends Enumeration {
    val Call, Return, Cont = Value
  }
}
