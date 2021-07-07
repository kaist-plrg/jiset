package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.{ DEBUG, TIMEOUT, TEST_MODE }
import kr.ac.kaist.jiset.js.ast.Lexical
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.error.NotSupported
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.js.{ Parser => ESParser, _ }
import kr.ac.kaist.jiset.parser.ESValueParser
import scala.collection.mutable.{ Map => MMap }
import scala.annotation.tailrec

// IR Interpreter
private class Interp(
  st: State,
  filename: String = "unknown",
  timeLimit: Option[Long] = Some(TIMEOUT)
) {
  import Interp._

  // set start time of interpreter
  val startTime: Long = System.currentTimeMillis

  // the number of instructions
  def getInstCount: Int = instCount
  var instCount: Int = 0

  // iteration period for check
  val CHECK_PERIOD = 10000

  // start
  fixpoint

  // perform transition until instructions are empty
  @tailrec
  final def fixpoint: Unit = st.context.insts match {
    case Nil => st.ctxtStack match {
      case Nil =>
      case _ => {
        doReturn(Undef)
        fixpoint
      }
    }
    case inst :: rest => {
      st.context.insts = rest
      interp(inst)
      fixpoint
    }
  }

  // transition for instructions
  def interp(inst: Inst): Unit = try {
    instCount += 1
    if (instCount % CHECK_PERIOD == 0) timeLimit.map(limit => {
      val duration = (System.currentTimeMillis - startTime) / 1000
      if (duration > limit) error("TIMEOUT")
    })
    if (DEBUG) inst match {
      case ISeq(_) =>
      case _ => println(s"${st.context.name}: ${inst.beautified}")
    }
    inst match {
      case IIf(cond, thenInst, elseInst) =>
        interp(cond).escaped(st) match {
          case Bool(true) => st.context.insts ::= thenInst
          case Bool(false) => st.context.insts ::= elseInst
          case v => error(s"not a boolean: ${v.beautified}")
        }
      case IWhile(cond, body) => interp(cond).escaped(st) match {
        case Bool(true) => st.context.insts = body :: inst :: st.context.insts
        case Bool(false) =>
        case v => error(s"not a boolean: ${v.beautified}")
      }
      case IApp(id, ERef(RefId(Id(name))), args) if simpleFuncs contains name => {
        val vs =
          if (name == "IsAbruptCompletion") args.map(interp)
          else args.map(interp(_).escaped(st))
        st.context.locals += id -> simpleFuncs(name)(st, vs)
      }
      case IApp(id, fexpr, args) => interp(fexpr) match {
        case Func(algo) => {
          val head = algo.head
          val body = algo.getBody
          val vs = args.map(interp)
          val locals = getLocals(head.params, vs)
          val context = Context(id, head.name, List(body), locals)
          st.ctxtStack ::= st.context
          st.context = context
        }
        case Clo(ctxtName, params, locals, body) => {
          val vs = args.map(interp)
          val newLocals = locals ++ getLocals(params.map(x => Param(x.name)), vs)
          val context = Context(id, ctxtName + ":closure", List(body), locals)
          st.ctxtStack ::= st.context
          st.context = context
        }
        case Cont(params, body, context, ctxtStack) => {
          val vs = args.map(interp)
          st.context = context.copied
          st.context.insts = List(body)
          st.context.locals ++= params zip vs
          st.ctxtStack = ctxtStack.map(_.copied)
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
            case ("(IdentifierName \\ (ReservedWord))" | "IdentifierName", "StringValue") => Str(ESValueParser.parseIdentifier(str))
            case ("NumericLiteral", "MV" | "NumericValue") => Num(ESValueParser.parseNumber(str))
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
          case (ASTVal(ast), Str(name)) => ast.semantics(name) match {
            case Some((algo, asts)) => {
              val head = algo.head
              val body = algo.getBody
              val vs = asts ++ args.map(interp)
              val locals = getLocals(head.params, vs)
              val context = Context(id, head.name, List(body), locals)
              st.ctxtStack ::= st.context
              st.context = context
              None
            }
            case None => Some(ast.subs(name).getOrElse {
              error(s"unexpected semantics: ${ast.name}.$name")
            })
          }
          case (addr: Addr, _) => Some(st(addr, prop))
          case (Str(str), _) => Some(st(str, prop))
          case v => error(s"invalid access: ${inst.beautified}")
        }
        vOpt.map(st.context.locals += id -> _)
      }
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
      case IReturn(expr) => doReturn(interp(expr))
      case IThrow(name) => {
        val addr = st.allocMap(Ty("OrdinaryObject"), Map(
          Str("Prototype") -> NamedAddr(s"GLOBAL.$name.prototype"),
          Str("ErrorData") -> Undef
        ))
        doReturn(addr.wrapCompletion(st, CompletionType.Throw))
      }
      case IAssert(expr) => interp(expr).escaped(st) match {
        case Bool(true) =>
        case v => error(s"assertion failure: ${expr.beautified}")
      }
      case IPrint(expr) => {
        val v = interp(expr)
        if (!TEST_MODE) println(st.getString(v))
      }
      case IWithCont(id, params, bodyInst) => {
        val State(context, ctxtStack, _, _) = st
        st.context = context.copied
        st.context.insts = List(bodyInst)
        st.context.locals += id -> Cont(params, ISeq(context.insts), context, ctxtStack)
        st.ctxtStack = ctxtStack.map(_.copied)
      }
      case ISeq(insts) => st.context.insts = insts ++ st.context.insts
    }
    if (instCount % 100000 == 0) GC.gc(st)
  } catch { case ReturnValue(value) => doReturn(value) }

  // return value
  private case class ReturnValue(value: Value) extends Throwable

  // return helper
  def doReturn(value: Value): Unit = st.ctxtStack match {
    case Nil => error(s"no remaining calling contexts")
    case ctxt :: rest => {
      // proper type handle
      (value, setTypeMap.get(st.context.name)) match {
        case (addr: Addr, Some(ty)) => st.setType(addr, ty)
        case _ =>
      }

      ctxt.locals += st.context.retId -> value.wrapCompletion(st)
      st.context = ctxt
      st.ctxtStack = rest
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
    case EList(exprs) => st.allocList(exprs.map(interp))
    case ESymbol(desc) => interp(desc) match {
      case (str: Str) => st.allocSymbol(str)
      case v => error(s"not a string: ${v.beautified}")
    }
    case EPop(list, idx) => interp(list).escaped(st) match {
      case (addr: Addr) => st.pop(addr, interp(idx).escaped(st))
      case v => error(s"not an address: ${v.beautified}")
    }
    case ERef(ref) => st(interp(ref))
    case EClo(params, captured, body) =>
      Clo(st.context.name, params, MMap.from(captured.map(x => x -> st(x))), body)
    case ECont(params, body) =>
      Cont(params, body, st.context.copied, st.ctxtStack.map(_.copied))
    case EUOp(uop, expr) => {
      val x = interp(expr).escaped(st)
      interp(uop, x)
    }
    case EBOp(OAnd, left, right) => shortCircuit(OAnd, left, right)
    case EBOp(OOr, left, right) => shortCircuit(OOr, left, right)
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
      case Cont(_, _, _, _) => "Continuation"
      case ASTVal(_) => "AST"
    })
    case EIsCompletion(expr) => Bool(interp(expr).isCompletion(st))
    case EIsInstanceOf(base, name) => interp(base).escaped(st) match {
      case ASTVal(ast) => Bool(ast.name == name || ast.getKinds.contains(name))
      case Str(str) => Bool(str == name)
      case addr: Addr => st(addr) match {
        case IRMap(ty, _, _) => Bool(ty < Ty(name))
        case _ => Bool(false)
      }
      case _ => Bool(false)
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
        case ASTVal(ast) => ASTVal(timeout(
          ESParser.parse(p(ast.parserParams), ast.toString).get,
          timeLimit
        ).checkSupported)
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
          ASTVal(timeout(
            ESParser.parse(p(ps), str).get,
            timeLimit
          ).checkSupported)
        case v => error(s"not an AST value or a string: $v")
      }
    }
    case EConvert(source, target, flags) => interp(source).escaped(st) match {
      case Str(s) => target match {
        case CStrToNum => Num(ESValueParser.str2num(s))
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
          case _ => error(s"not convertable option: INum to $target")
        }
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
    case (ONot, Bool(b)) => Bool(!b)
    case (OBNot, Num(n)) => INum(~(n.toInt))
    case (OBNot, INum(n)) => INum(~n)
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
    case (OEq, l, r) => Bool(l == r)

    // double equality operations
    case (OEqual, INum(l), Num(r)) => Bool(l == r)
    case (OEqual, Num(l), INum(r)) => Bool(l == r)
    case (OEqual, Num(l), Num(r)) => Bool(l == r)
    case (OEqual, l, r) => Bool(l == r)

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
}
object Interp {
  def apply(
    st: State,
    filename: String = "unknown",
    timeLimit: Option[Long] = Some(TIMEOUT)
  ): State = {
    new Interp(st, filename, timeLimit)
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
    arityCheck("IsDuplicate" -> {
      case (st, List(addr: Addr)) => st(addr) match {
        case IRList(vs) => Bool(vs.toSet.size != vs.length)
        case _ => error(s"non-list @ IsDuplicate: ${addr.beautified}")
      }
    }),
    arityCheck("IsArrayIndex" -> {
      case (st, List(Str(s))) =>
        val d = ESValueParser.str2num(s)
        val UPPER = (1L << 32) - 1
        val l = d.toLong
        Bool(0 <= l && d == l && l < UPPER)
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
}
