package kr.ac.kaist.jiset.editor.analyzer

import kr.ac.kaist.jiset.DEBUG
import kr.ac.kaist.jiset.editor.analyzer.domain._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir.{ AllocSite => _, _ }
import kr.ac.kaist.jiset.js.{ Parser => ESParser, _ }
import kr.ac.kaist.jiset.js.ast.{ Lexical, AST }
import kr.ac.kaist.jiset.parser.ESValueParser
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec.algorithm._
import scala.annotation.tailrec
import kr.ac.kaist.jiset.error.JISETError

// abstract transfer function
case class AbsTransfer(sem: AbsSemantics) {
  // loading monads
  import AbsState.monad._

  // math value to numeric
  import NumericConverter._

  // transfer function for control points
  def apply(cp: ControlPoint): Unit = cp match {
    case (np: NodePoint[_]) => this(np)
    case (rp: ReturnPoint) => this(rp)
  }

  // transfer function for node points
  def apply[T <: Node](np: NodePoint[T]): Unit = {
    val st = sem(np)
    val NodePoint(node) = np
    val helper = new Helper(np)

    import helper._
    node match {
      case (entry: Entry) =>
        sem += getNextNp(np, cfg.nextOf(entry)) -> st
      case (exit: Exit) =>
        doReturn(AbsValue.undef)(st)
      case (normal: Normal) =>
        val newSt = transfer(normal.inst)(st)
        sem += getNextNp(np, cfg.nextOf(normal)) -> newSt
      case (call: Call) =>
        val newSt = transfer(call)(st)
        sem += getNextNp(np, cfg.nextOf(call)) -> newSt
      case arrow @ Arrow(_, inst, fid) =>
        val newSt = transfer(arrow, np)(st)
        sem += getNextNp(np, cfg.nextOf(arrow)) -> newSt
      case branch @ Branch(_, inst) => (for {
        v <- escape(transfer(inst.cond))
        st <- get
      } yield {
        val (thenNode, elseNode) = cfg.branchOf(branch)
        if (AbsValue(Bool(true)) ⊑ v) sem += getNextNp(np, thenNode) -> st
        if (AbsValue(Bool(false)) ⊑ v) sem += getNextNp(np, elseNode) -> st
      })(st)
      case (cont: LoopCont) =>
        sem += getNextNp(np, cfg.nextOf(cont)) -> st
    }
  }

  // get next node points
  def getNextNp(
    fromCp: NodePoint[Node],
    to: Node
  ): NodePoint[Node] = {
    NodePoint(to)
  }

  // transfer function for return points
  def apply(rp: ReturnPoint): Unit = {
    var ret @ AbsRet(value, st) = sem(rp)

    // debugging message
    if (DEBUG) println(s"<RETURN> $ret")

    // return wrapped values
    for (np @ NodePoint(call) <- sem.getRetEdges(rp)) {
      val callerSt = sem.callInfo(np)
      val nextNode = cfg.nextOf(call)
      val nextNp = NodePoint(nextNode)

      val newSt = st.doReturn(
        callerSt,
        call.inst.id -> value.wrapCompletion(None)
      )

      sem += nextNp -> newSt
    }
  }

  // transfer function for expressions
  def apply(cp: ControlPoint, expr: Expr): AbsValue = {
    val st = sem.getState(cp)
    val helper = new Helper(cp)
    helper.transfer(expr)(st)._1
  }

  // internal transfer function with a specific view
  class Helper(val cp: ControlPoint) {
    lazy val func = cp.func
    lazy val rp = ReturnPoint(func.origin match {
      case ArrowOrigin(algo, inst) if inst.isContinuation => algo.func
      case _ => func
    })

    // transfer function for normal instructions
    def transfer(inst: NormalInst): Updater = inst match {
      case IExpr(expr) => for {
        v <- transfer(expr)
      } yield v
      case ILet(id, expr) => for {
        v <- transfer(expr)
        _ <- modify(_.defineLocal(id -> v))
      } yield ()
      case IAssign(ref, expr) => for {
        rv <- transfer(ref)
        v <- transfer(expr)
        _ <- modify(_.update(rv, v))
      } yield ()
      case IDelete(ref) => for {
        rv <- transfer(ref)
        _ <- modify(_.delete(rv))
      } yield ()
      case IAppend(expr, list) => for {
        l <- escape(transfer(list))
        v <- escape(transfer(expr))
        _ <- modify(_.append(l, v))
      } yield ()
      case IPrepend(expr, list) => for {
        l <- escape(transfer(list))
        v <- escape(transfer(expr))
        _ <- modify(_.prepend(l, v))
      } yield ()
      case IReturn(expr) => for {
        v <- transfer(expr)
        _ <- doReturn(v)
        _ <- put(AbsState.Bot)
      } yield ()
      case thr @ IThrow(name) => {
        for {
          _ <- doReturn(AbsValue.Top)
          _ <- put(AbsState.Bot)
        } yield ()
      }
      case IAssert(expr) => for {
        v <- transfer(expr)
      } yield ()
      case IPrint(expr) => st => st
    }

    // return specific value
    def doReturn(v: AbsValue): Result[Unit] = for {
      st <- get
      ret = AbsRet(v, st.copy(locals = Map()))
      _ = sem.doReturn(rp, ret)
    } yield ()

    // transfer function for calls
    def transfer(call: Call): Updater = call.inst match {
      case IApp(id, ERef(RefId(Id(name))), args) if simpleFuncs contains name => {
        for {
          as <- join(args.map(transfer))
          vs = if (name == "IsAbruptCompletion") as else as.map(_.escaped)
          st <- get
          v <- simpleFuncs(name)(vs)
          _ <- modify(_.defineLocal(id -> v))
        } yield ()
      }
      case IApp(id, fexpr, args) => for {
        value <- transfer(fexpr)
        vs <- join(args.map(transfer))
        st <- get
        v: AbsValue = {
          // return values
          var returnValue: AbsValue = AbsValue.Bot

          value.getSingle match {
            case FlatBot => ()
            case FlatTop => returnValue = AbsValue.Top
            case FlatElem(Func(algo)) => {
              val locals = getLocals(algo.head.params, vs)
              if (locals.forall { case (_, v) => v.getSingle.isInstanceOf[FlatElem[_]] }) {

                // try dynamic shortcut ~
                var initSt = kr.ac.kaist.jiset.js.Initialize.initSt.copy()
                initSt = initSt.copy(context = Context(
                  locals = collection.mutable.Map() ++ locals.map { case (k, v) => k -> v.getSingle.asInstanceOf[FlatElem[Value]].elem },
                  cursorOpt = Some(initSt.cursorGen(algo.body))
                ))
                try {
                  val st = DSInterp(initSt, Some(100))
                  st.context.locals.get(Id(RESULT)) match {
                    case Some(value) => returnValue = AbsValue(value)
                    case None => ()
                  }
                } catch {
                  case _: JISETError => ()
                }
              }
              if (returnValue.isBottom) {
                val newLocals = getLocals(algo.head.params, vs)
                val newSt = st.copy(locals = newLocals)
                sem.doCall(call, st, algo.func, newSt)
              }
            }
            case _ => returnValue = AbsValue.Top
          }

          returnValue
        }
        _ <- {
          if (v.isBottom) put(AbsState.Bot)
          else modify(_.defineLocal(id -> v))
        }
      } yield ()
      case access @ IAccess(id, bexpr, expr, args) => {
        val loc: AllocSite = AllocSite(access.asite)
        for {
          origB <- transfer(bexpr)
          b = origB.escaped
          p <- escape(transfer(expr))
          value <- (b.getSingleAST, p.getSingle) match {
            case (FlatElem(ast), FlatElem(Str(name))) => (ast, name) match {
              case (Lexical(kind, str), name) =>
                pure(AbsValue(Interp.getLexicalValue(kind, name, str)))
              case (ast, "parent") =>
                pure(ast.parent.map((x) => AbsValue(ASTVal(x))).getOrElse(AbsValue.absent))
              case (ast, "children") => pure(AbsValue.Top)
              case (ast, "kind") =>
                pure(AbsValue(Str(ast.kind)))
              case _ => ast.semantics(name) match {
                case Some((algo, asts)) => for {
                  as <- join(args.map(transfer))
                  head = algo.head
                  body = algo.body
                  vs = asts.map(AbsValue(_)) ++ as
                  locals = getLocals(head.params, vs)
                  st <- get
                  newSt <- get(_.copy(locals = locals))
                  astOpt = (
                    if (name == "Evaluation" || name == "NamedEvaluation") Some(ast)
                    else None
                  )
                  v <- {
                    var returnValue: AbsValue = AbsValue.Bot
                    if (locals.forall { case (_, v) => v.getSingle.isInstanceOf[FlatElem[_]] }) {
                      var initSt = kr.ac.kaist.jiset.js.Initialize.initSt.copy()
                      initSt = initSt.copy(context = Context(
                        locals = collection.mutable.Map() ++ locals.map { case (k, v) => k -> v.getSingle.asInstanceOf[FlatElem[Value]].elem },
                        cursorOpt = Some(initSt.cursorGen(algo.body))
                      ))

                      try {
                        val st = DSInterp(initSt, Some(100))
                        st.context.locals.get(Id(RESULT)) match {
                          case Some(value) => returnValue = AbsValue(value)
                          case None => ()
                        }
                      } catch {
                        case e: JISETError => ()
                      }
                    }
                    if (returnValue.isBottom) {
                      sem.doCall(call, st, algo.func, newSt, astOpt).flatMap((_) => AbsValue.Bot)
                    } else {
                      pure(returnValue)
                    }
                  }
                } yield v
                case None =>
                  val v = ast.subs(name).map(AbsValue(_)).getOrElse(AbsValue.Top)
                  pure(v)
              }
            }
            case (FlatBot, _) | (_, FlatBot) => pure(AbsValue.Bot)
            case _ => pure(AbsValue.Top)
          }
          _ <- {
            if (!value.isBottom) modify(_.defineLocal(id -> value))
            else put(AbsState.Bot)
          }
        } yield ()
      }
    }

    // transfer function for arrow instructions
    def transfer(arrow: Arrow, np: NodePoint[Node]): Updater = arrow.inst match {
      case IClo(id, params, captured, body) => for {
        st <- get
        _ <- modify(_.defineLocal(id -> AbsValue.Top))
      } yield ()
      case ICont(id, params, body) => for {
        locals <- get(_.locals)
        _ <- modify(_.defineLocal(id -> AbsValue.Top))
      } yield ()
      case IWithCont(id, params, body) => for {
        locals <- get(_.locals)
        _ <- modify(_.defineLocal(id -> AbsValue.Top))
        st <- get
        _ = sem += NodePoint(cfg.bodyFuncMap(body.uid).entry) -> st
        _ <- put(AbsState.Bot)
      } yield ()
    }

    // transfer function for expressions
    def transfer(expr: Expr): Result[AbsValue] = expr match {
      case ENum(n) => AbsValue(Num(n))
      case EINum(l) => AbsValue(INum(l))
      case EBigINum(b) => AbsValue(BigINum(b))
      case EStr(str) => AbsValue(Str(str))
      case EBool(b) => AbsValue(Bool(b))
      case EUndef => AbsValue.undef
      case ENull => AbsValue.nullv
      case EAbsent => AbsValue.absent
      case EConst(name) => AbsValue(Const(name))
      case EComp(ty, value, target) => for {
        y <- escape(transfer(ty))
        v <- escape(transfer(value))
        origT <- escape(transfer(target))
      } yield (y.getSingle, v.getSingle, origT.getSingle) match {
        case (FlatElem(Const(ty)), FlatElem(value: PureValue), FlatElem(Str(target))) =>
          AbsValue(CompValue(Const(ty), value, Some(target)))
        case (FlatElem(Const(ty)), FlatElem(value: PureValue), FlatElem(Absent)) =>
          AbsValue(CompValue(Const(ty), value, None))
        case _ => AbsValue.Top
      }
      case map @ EMap(ty, props) => AbsValue.Top
      case list @ EList(exprs) => AbsValue.Top
      case symbol @ ESymbol(desc) => AbsValue.Top
      case EPop(list, idx) => AbsValue.Top
      case ERef(ref) => for {
        rv <- transfer(ref)
        v <- transfer(rv)
      } yield v
      case EUOp(uop, expr) => for {
        x <- escape(transfer(expr))
        v <- get(transfer(_, uop, x))
      } yield v
      case EBOp(OAnd, left, right) => shortCircuit(OAnd, left, right)
      case EBOp(OOr, left, right) => shortCircuit(OOr, left, right)
      case EBOp(bop, left, right) => for {
        l <- escape(transfer(left))
        r <- escape(transfer(right))
        v <- get(transfer(_, bop, l, r))
      } yield v
      case ETypeOf(expr) => for {
        value <- escape(transfer(expr))
        st <- get
      } yield {
        value.getSingle match {
          case FlatBot => AbsValue.Bot
          case FlatTop => AbsValue.Top
          case FlatElem(elem: Value) => elem match {
            case CompValue(ty, value, targetOpt) => AbsValue(Str("Completion"))
            case Func(algo) => AbsValue(Str("Function"))
            case Const(name) => AbsValue(Str("Constant"))
            case Cont(params, context, ctxtStack) => AbsValue(Str("Continuation"))
            case ASTVal(ast) => AbsValue(Str("AST"))
            case DynamicAddr(long) => AbsValue.Top
            case NamedAddr(name) => AbsValue.Top
            case Clo(ctxtName, params, locals, cursorOpt) => AbsValue(Str("Closure"))
            case Undef => AbsValue(Str("Undefined"))
            case BigINum(b) => AbsValue(Str("BigInt"))
            case INum(long) => AbsValue(Str("Number"))
            case Num(double) => AbsValue(Str("Number"))
            case Null => AbsValue(Str("Null"))
            case Bool(bool) => AbsValue(Str("Boolean"))
            case Str(str) => AbsValue(Str("String"))
            case Absent => AbsValue(Str("Absent"))
          }
        }
      }
      case EIsCompletion(expr) => for {
        v <- transfer(expr)
      } yield {
        v.getSingle match {
          case FlatElem(CompValue(_, _, _)) => AbsValue(Bool(true))
          case FlatBot => AbsValue.Bot
          case _ => AbsValue(Bool(false))
        }
      }
      case EIsInstanceOf(base, name) => for {
        origB <- transfer(base)
      } yield origB.getSingle match {
        case FlatBot => AbsValue.Bot
        case FlatTop => AbsValue.Top
        case FlatElem(elem) => if (elem.isAbruptCompletion) AbsValue(Bool(false))
        else elem.escaped match {
          case ASTVal(ast) => AbsValue(Bool(ast.name == name || ast.getKinds.contains(name)))
          case Str(str) => AbsValue(Bool(str == name))
          case addr: Addr => AbsValue.Top
          case _ => AbsValue(Bool(false))
        }
      }
      case elems @ EGetElems(base, name) => AbsValue.Top
      case EGetSyntax(base) => for {
        v <- escape(transfer(base))
        s = v.getSingle match {
          case FlatElem(ASTVal(ast)) => AbsValue(Str(ast.toString))
          case FlatBot => AbsValue.Bot
          case _ => AbsValue.Top
        }
      } yield s
      case EParseSyntax(code, rule, parserParams) => for {
        v <- escape(transfer(code))
        ruleV <- escape(transfer(rule))
        p = ruleV.getSingle match {
          case FlatElem(Str(str)) =>
            Some(ESParser.rules.getOrElse(str, error(s"not exist parse rule: $rule")))
          case _ => None
        }
        st <- get
      } yield v.getSingle match {
        case FlatElem(ASTVal(ast)) =>
          p.map((p) => AbsValue(Interp.doParseAst(p(ast.parserParams))(ast))).getOrElse(AbsValue.Top)
        case FlatElem(Str(str)) => {
          p.map((p) => AbsValue(Interp.doParseStr(p(parserParams))(str))).getOrElse(AbsValue.Top)
        }
        case v => error(s"not an AST value or a string: $v")
      }
      case EConvert(source, target, flags) => AbsValue.Top
      case EContains(list, elem) => AbsValue.Top
      case EReturnIfAbrupt(rexpr @ ERef(ref), check) => for {
        rv <- transfer(ref)
        v <- transfer(rv)
        newV <- returnIfAbrupt(v, check)
        _ <- modify(_.update(rv, newV))
      } yield newV
      case EReturnIfAbrupt(expr, check) => for {
        v <- transfer(expr)
        newV <- returnIfAbrupt(v, check)
      } yield newV
      case copy @ ECopy(obj) => AbsValue.Top
      case keys @ EKeys(mobj, intSorted) => AbsValue.Top
      case ENotSupported(msg) => AbsValue.Bot
    }

    // return if abrupt completion
    def returnIfAbrupt(
      value: AbsValue,
      check: Boolean
    ): Result[AbsValue] = {
      val checkReturn: Result[Unit] =
        if (check) doReturn(value.getSingle match {
          case FlatBot => AbsValue.Bot
          case FlatTop => AbsValue.Top
          case FlatElem(NormalComp(_)) => AbsValue.Bot
          case FlatElem(CompValue(_, _, _)) => value
          case _ => AbsValue.Bot
        })
      val newValue = value.getSingle match {
        case FlatBot => AbsValue.Bot
        case FlatTop => AbsValue.Top
        case FlatElem(NormalComp(v)) => AbsValue(v)
        case FlatElem(CompValue(_, _, _)) => AbsValue.Bot
        case _ => value
      }
      for (_ <- checkReturn) yield newValue
    }

    // transfer function for references
    def transfer(ref: Ref): Result[AbsRefValue] = ref match {
      case RefId(id) => AbsRefId(id)
      case RefProp(ref, expr) => for {
        rv <- transfer(ref)
        b <- transfer(rv)
        p <- escape(transfer(expr))
      } yield AbsRefProp(b, p)
    }

    // unary operators
    def transfer(
      st: AbsState,
      uop: UOp,
      operand: AbsValue
    ): AbsValue = operand.getSingle match {
      case FlatBot => AbsValue.Bot
      case FlatElem(x) =>
        AbsValue(Interp.interp(uop, x))
      case FlatTop => AbsValue.Top
    }

    // binary operators
    def transfer(
      st: AbsState,
      bop: BOp,
      left: AbsValue,
      right: AbsValue
    ): AbsValue = (left.getSingle, right.getSingle) match {
      case (FlatBot, _) | (_, FlatBot) => AbsValue.Bot
      case (FlatElem(l), FlatElem(r)) =>
        AbsValue(Interp.interp(bop, l, r))
      case _ => AbsValue.Top
    }

    // transfer function for reference values
    def transfer(rv: AbsRefValue): Result[AbsValue] = for {
      v <- get(_(rv, cp))
    } yield v

    // short circuit evaluation
    def shortCircuit(
      bop: BOp,
      left: Expr,
      right: Expr
    ): Result[AbsValue] = for {
      l <- escape(transfer(left))
      v <- (bop, l.getSingle) match {
        case (OAnd, FlatElem(Bool(false))) => pure(AbsValue(Bool(false)))
        case (OOr, FlatElem(Bool(true))) => pure(AbsValue(Bool(true)))
        case _ => for {
          r <- escape(transfer(right))
          v <- get(transfer(_, bop, l, r))
        } yield v
      }
    } yield v

    // get initial local variables
    def getLocals(
      params: List[Param],
      args: List[AbsValue]
    ): Map[Id, AbsValue] = {
      var map = Map[Id, AbsValue]()

      @tailrec
      def aux(ps: List[Param], as: List[AbsValue]): Unit = (ps, as) match {
        case (Nil, Nil) =>
        case (Param(name, kind) :: pl, Nil) => {
          map += Id(name) -> AbsValue.absent
          aux(pl, Nil)
        }
        case (param :: pl, arg :: al) => {
          map += Id(param.name) -> arg
          aux(pl, al)
        }
        case _ =>
      }

      aux(params, args)
      map
    }
  }

  // escape completions
  def escape(value: Result[AbsValue]): Result[AbsValue] = for {
    v <- value
  } yield v.escaped

  // simple functions
  type SimpleFunc = List[AbsValue] => Result[AbsValue]
  val simpleFuncs: Map[String, SimpleFunc] = {
    Map(
      "GetArgument" -> {
        case List(v) => AbsValue.Top
      },
      // TODO fix bug
      "IsDuplicate" -> {
        case List(v) => AbsValue.Top
      },
      "IsArrayIndex" -> {
        case List(v) => AbsValue.Top
      },
      "min" -> {
        case List(v0, v1) => (v0.getSingle, v1.getSingle) match {
          case (_, FlatBot) | (FlatBot, _) => AbsValue.Bot
          case (FlatElem(n0: Numeric), FlatElem(n1: Numeric)) =>
            AbsValue(n0.min(n1))
          case _ => AbsValue.Top
        }
      },
      "max" -> {
        case List(v0, v1) => (v0.getSingle, v1.getSingle) match {
          case (_, FlatBot) | (FlatBot, _) => AbsValue.Bot
          case (FlatElem(n0: Numeric), FlatElem(n1: Numeric)) =>
            AbsValue(n0.max(n1))
          case _ => AbsValue.Top
        }
      },
      "abs" -> {
        case List(v) => v.getSingle match {
          case FlatBot => AbsValue.Bot
          case FlatElem(n: Numeric) => AbsValue(n.abs)
          case _ => AbsValue.Top
        }
      },
      "floor" -> {
        case List(v) => v.getSingle match {
          case FlatBot => AbsValue.Bot
          case FlatElem(n: Numeric) => AbsValue(n.floor)
          case _ => AbsValue.Top
        }
      },
      "fround" -> { case args => AbsValue.Top },
      "ThrowCompletion" -> {
        case List(value) => AbsValue.Top //value.wrapCompletion("throw")
      },
      "NormalCompletion" -> {
        case List(value) => AbsValue.Top // pure(value.wrapCompletion)
      },
      "IsAbruptCompletion" -> {
        case List(value) => AbsValue.Top //pure(AbsValue(bool = value.isAbruptCompletion))
      },
    )
  }
}
