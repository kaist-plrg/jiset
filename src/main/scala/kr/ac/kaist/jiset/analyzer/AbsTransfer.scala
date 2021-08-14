package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.DEBUG
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir.{ AllocSite => _, _ }
import kr.ac.kaist.jiset.js.{ Parser => ESParser, _ }
import kr.ac.kaist.jiset.js.ast.Lexical
import kr.ac.kaist.jiset.parser.ESValueParser
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec.algorithm._
import scala.annotation.tailrec

// abstract transfer function
case class AbsTransfer(sem: AbsSemantics) {
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
    val NodePoint(node, view) = np
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
        val nextNp = getNextNp(np, cfg.nextOf(arrow))
        val newSt = transfer(arrow, nextNp)(st)
        sem += nextNp -> newSt
      case branch @ Branch(_, inst) => (for {
        v <- escape(transfer(inst.cond))
        b = v.bool
        st <- get
      } yield {
        val (thenNode, elseNode) = cfg.branchOf(branch)
        if (b contains T) sem += getNextNp(np, thenNode) -> st
        if (b contains F) sem += getNextNp(np, elseNode, true) -> st
      })(st)
      case (cont: LoopCont) =>
        sem += getNextNp(np, cfg.nextOf(cont)) -> st
    }
  }

  // get next node points
  def getNextNp(
    fromCp: NodePoint[Node],
    to: Node,
    loopOut: Boolean = false
  ): NodePoint[Node] = {
    val NodePoint(from, view) = fromCp
    val toView = (from, to) match {
      case (_: LoopCont, _: Loop) => view.loopNext
      case (_, loop: Loop) => view.loopEnter(loop)
      case (_: Loop, _) if loopOut => view.loopExit
      case _ => view
    }
    NodePoint(to, toView)
  }

  // transfer function for return points
  def apply(rp: ReturnPoint): Unit = {
    var ret @ AbsRet(value, st) = sem(rp)

    // proper type handle
    Interp.setTypeMap.get(rp.func.name).map(ty => {
      value = AbsValue(loc = value.loc)
      st = st.setType(value.loc, ty)
    })

    // debugging message
    if (DEBUG) println(s"<RETURN> $ret")

    // return wrapped values
    for (np @ NodePoint(call, view) <- sem.getRetEdges(rp)) {
      val callerSt = sem.callInfo(np)
      val nextNode = cfg.nextOf(call)
      val nextNP = NodePoint(nextNode, nextNode match {
        case loop: Loop => view.loopEnter(loop)
        case _ => view
      })

      val newSt = st.doReturn(
        callerSt,
        call.inst.id -> value.wrapCompletion
      )

      sem += nextNP -> newSt
    }
  }

  // internal transfer function with a specific view
  private class Helper(val cp: ControlPoint) {
    lazy val func = sem.funcOf(cp)
    lazy val view = cp.view
    lazy val rp = ReturnPoint(func.origin match {
      case ArrowOrigin(algo, inst) if inst.isContinuation => algo.func
      case _ => func
    }, view)

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
        loc = l.loc
        v <- escape(transfer(expr))
        _ <- modify(_.append(l.loc, v))
      } yield ()
      case IPrepend(expr, list) => for {
        l <- escape(transfer(list))
        loc = l.loc
        v <- escape(transfer(expr))
        _ <- modify(_.prepend(l.loc, v))
      } yield ()
      case IReturn(expr) => for {
        v <- transfer(expr)
        _ <- doReturn(v)
        _ <- put(AbsState.Bot)
      } yield ()
      case thr @ IThrow(name) => {
        val loc: AllocSite = AllocSite(thr.asite, cp.view)
        for {
          _ <- modify(_.allocMap(Ty("OrdinaryObject"), List(
            AbsValue(Str("Prototype")) -> AbsValue(NamedLoc(s"GLOBAL.$name.prototype")),
            AbsValue(Str("ErrorData")) -> AbsValue(Undef),
          ))(loc))
          _ <- doReturn(AbsValue(loc).wrapCompletion("throw"))
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
      ret = AbsRet(v, st)
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
        _ <- put(AbsState.Bot)
      } yield {
        // algorithms
        for (AFunc(algo) <- value.func) {
          val newLocals = getLocals(algo.head.params, vs)
          val newSt = st.copy(locals = newLocals)
          sem.doCall(call, view, st, algo.func, newSt)
        }

        // closures
        for (AClo(params, locals, func) <- value.clo) {
          val newLocals = locals ++ getLocals(params.map(x => Param(x.name)), vs)
          val newSt = st.copy(locals = newLocals)
          sem.doCall(call, view, st, func, newSt)
        }

        // continuations
        for (ACont(params, locals, target) <- value.cont) target match {
          // start/resume sub processes
          case NodePoint(entry: Entry, targetView) =>
            sem += target -> st.copy(locals = locals ++ (params zip vs))

          // stop/pause sub processes
          case NodePoint(_, targetView) =>
            sem += target -> st.copy(locals = locals ++ (params zip vs))
        }
      }
      case access @ IAccess(id, bexpr, expr, args) => {
        val loc: AllocSite = AllocSite(access.asite, cp.view)
        for {
          origB <- transfer(bexpr)
          b = origB.escaped
          p <- escape(transfer(expr))
          astV <- (b.ast.getSingle, p.str.getSingle) match {
            case (FlatElem(AAst(ast)), FlatElem(Str(name))) => (ast, name) match {
              case (Lexical(kind, str), name) =>
                pure(AbsValue(Interp.getLexicalValue(kind, name, str)))
              case (ast, "parent") =>
                pure(ast.parent.map(AbsValue(_)).getOrElse(AbsValue.absent))
              case (ast, "children") => for {
                _ <- modify(_.allocList(ast.children.map(AbsValue(_)))(loc))
              } yield AbsValue(loc)
              case (ast, "kind") =>
                pure(AbsValue(ast.kind))
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
                  _ = sem.doCall(call, view, st, algo.func, newSt, astOpt)
                } yield AbsValue.Bot
                case None =>
                  val v = AbsValue(ast.subs(name).getOrElse {
                    error(s"unexpected semantics: ${ast.name}.$name")
                  })
                  pure(v)
              }
            }
            case (FlatBot, _) | (_, FlatBot) => pure(AbsValue.Bot)
            case _ => error("impossible to handle generic access of ASTs")
          }
          otherV <- get(_(origB, p))
          value = astV ⊔ otherV
          _ <- {
            if (!value.isBottom) modify(_.defineLocal(id -> value))
            else put(AbsState.Bot)
          }
        } yield ()
      }
    }

    // transfer function for arrow instructions
    def transfer(arrow: Arrow, nextNp: NodePoint[Node]): Updater = arrow.inst match {
      case IClo(id, params, captured, body) => for {
        st <- get
        _ <- modify(_.defineLocal(id -> AbsValue(AClo(
          params,
          captured.map(x => x -> st(x, cp)).toMap,
          cfg.bodyFuncMap(body.uid),
        ))))
      } yield ()
      case ICont(id, params, body) => for {
        locals <- get(_.locals)
        _ <- modify(_.defineLocal(id -> AbsValue(ACont(
          params,
          locals,
          NodePoint(cfg.bodyFuncMap(body.uid).entry, view)
        ))))
      } yield ()
      case IWithCont(id, params, body) => for {
        locals <- get(_.locals)
        _ <- modify(_.defineLocal(id -> AbsValue(ACont(
          params,
          locals,
          nextNp
        ))))
        st <- get
        _ = sem += NodePoint(cfg.bodyFuncMap(body.uid).entry, view) -> st
        _ <- put(AbsState.Bot)
      } yield ()
    }

    // transfer function for expressions
    def transfer(expr: Expr): Result[AbsValue] = expr match {
      case ENum(n) => AbsValue(Num(n))
      case EINum(l) => AbsValue(l)
      case EBigINum(b) => AbsValue(b)
      case EStr(str) => AbsValue(str)
      case EBool(b) => AbsValue(b)
      case EUndef => AbsValue.undef
      case ENull => AbsValue.nullv
      case EAbsent => AbsValue.absent
      case EConst(name) => AbsValue(AConst(name))
      case EComp(ty, value, target) => for {
        y <- escape(transfer(ty))
        v <- escape(transfer(value))
        origT <- escape(transfer(target))
        t = AbsValue(str = origT.str, const = origT.const)
      } yield AbsValue(comp = AbsComp((for {
        AConst(name) <- y.const.toList
      } yield name -> AbsComp.Result(v, t)).toMap))
      case map @ EMap(ty, props) => {
        val loc: AllocSite = AllocSite(map.asite, cp.view)
        for {
          pairs <- join(props.map {
            case (kexpr, vexpr) => for {
              k <- transfer(kexpr)
              v <- transfer(vexpr)
            } yield (k, v)
          })
          _ <- modify(_.allocMap(ty, pairs)(loc))
        } yield AbsValue(loc)
      }
      case list @ EList(exprs) => {
        val loc: AllocSite = AllocSite(list.asite, cp.view)
        for {
          vs <- join(exprs.map(transfer))
          _ <- modify(_.allocList(vs.map(_.escaped))(loc))
        } yield AbsValue(loc)
      }
      case symbol @ ESymbol(desc) => {
        val loc: AllocSite = AllocSite(symbol.asite, cp.view)
        for {
          v <- transfer(desc)
          newV = AbsValue(str = v.str, undef = v.undef)
          _ <- modify(_.allocSymbol(newV)(loc))
        } yield AbsValue(loc)
      }
      case EPop(list, idx) => for {
        l <- escape(transfer(list))
        loc = l.loc
        k <- escape(transfer(idx))
        v <- id(_.pop(loc, k))
      } yield v
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
      case EBOp(OEq, ERef(ref), EAbsent) => for {
        rv <- transfer(ref)
        b <- get(_.exists(rv))
      } yield AbsValue(bool = !b)
      case EBOp(bop, left, right) => for {
        l <- escape(transfer(left))
        r <- escape(transfer(right))
        v <- get(transfer(_, bop, l, r))
      } yield v
      case ETypeOf(expr) => for {
        value <- escape(transfer(expr))
        st <- get
      } yield value.getSingle match {
        case FlatBot => AbsValue.Bot
        case FlatElem(v) => AbsValue(v match {
          case _: AComp => "Completion"
          case _: AConst => "Constant"
          case loc: Loc => st(loc).getTy.name match {
            case name if name endsWith "Object" => "Object"
            case name => name
          }
          case _: AFunc => "Function"
          case _: AClo => "Closure"
          case _: ACont => "Continuation"
          case _: AAst => "AST"
          case ASimple(_: Num | _: INum) => "Number"
          case ASimple(_: BigINum) => "BigInt"
          case ASimple(_: Str) => "String"
          case ASimple(_: Bool) => "Boolean"
          case ASimple(Undef) => "Undefined"
          case ASimple(Null) => "Null"
          case ASimple(Absent) => "Absent"
        })
        case FlatTop => AbsValue.str
      }
      case EIsCompletion(expr) => for {
        v <- transfer(expr)
      } yield AbsValue(bool = v.isCompletion)
      case EIsInstanceOf(base, name) => for {
        origB <- transfer(base)
        b = origB.escaped
        st <- get
      } yield AbsValue(bool = AbsBool((for {
        Bool(bool) <- origB.isAbruptCompletion.toSet
        resB <- if (bool) Set(false) else {
          var set = Set[Boolean]()
          for (AAst(ast) <- b.ast.toList) {
            set += ast.name == name || ast.getKinds.contains(name)
          }
          for (Str(str) <- b.str.toList) set += str == name
          for (loc <- b.loc.toList) set += st(loc).getTy < Ty(name)
          val otherV = b.copy(
            ast = AbsAST.Bot,
            loc = AbsLoc.Bot,
            simple = b.simple.copy(str = AbsStr.Bot),
          )
          if (!otherV.isBottom) set += false
          set
        }
      } yield resB).map(Bool(_))))
      case EGetElems(base, name) => ???
      case EGetSyntax(base) => for {
        v <- escape(transfer(base))
        s = AbsStr(v.ast.toList.map(x => Str(x.ast.toString)))
      } yield AbsValue(str = s)
      case EParseSyntax(code, rule, parserParams) => for {
        v <- escape(transfer(code))
        ruleV <- escape(transfer(rule))
        p = ruleV.str.getSingle match {
          case FlatElem(Str(str)) =>
            ESParser.rules.getOrElse(str, error(s"not exist parse rule: $rule"))
          case _ => ???
        }
        st <- get
      } yield AbsValue(v.getSingle match {
        case FlatElem(AAst(ast)) =>
          ESParser.parse(p(ast.parserParams), ast.toString).get.checkSupported
        case FlatElem(ASimple(Str(str))) => {
          ESParser.parse(p(parserParams), str).get.checkSupported
        }
        case v => error(s"not an AST value or a string: $v")
      })
      case EConvert(source, target, flags) => for {
        s <- escape(transfer(source))
        fs <- join(for (flag <- flags) yield escape(transfer(flag)))
      } yield {
        var newV: AbsValue = AbsValue.Bot
        for (Str(str) <- s.str) newV ⊔= (target match {
          case CStrToNum => AbsValue(Num(ESValueParser.str2num(str)))
          case CStrToBigInt => AbsValue(ESValueParser.str2bigint(str))
          case _ => AbsValue.Bot
        })
        var doubleSet = Set[Double]()
        for (INum(long) <- s.int) doubleSet += long.toDouble
        for (Num(double) <- s.num) doubleSet += double
        for (n <- doubleSet) newV ⊔= (target match {
          case CNumToStr => AbsValue(toStringHelper(n, fs.map(_.getSingle) match {
            case FlatElem(ASimple(INum(n))) :: _ => n.toInt
            case FlatElem(ASimple(Num(n))) :: _ => n.toInt
            case _ => 10
          }))
          case CNumToInt =>
            AbsValue((math.signum(n) * math.floor(math.abs(n))).toLong)
          case CNumToBigInt =>
            AbsValue(BigInt(new java.math.BigDecimal(n).toBigInteger))
          case _ =>
            AbsValue.Bot
        })
        for (BigINum(b) <- s.bigint) newV ⊔= (target match {
          case CNumToBigInt => AbsValue(b)
          case CNumToStr => AbsValue(b.toString)
          case CBigIntToNum => AbsValue(Num(b.toDouble))
          case _ => AbsValue.Bot
        })
        newV
      }
      case EContains(list, elem) => for {
        l <- escape(transfer(list))
        v <- escape(transfer(elem))
        b <- get(_.contains(l.loc, v))
      } yield AbsValue(bool = b)
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
      case copy @ ECopy(obj) => {
        val loc: AllocSite = AllocSite(copy.asite, cp.view)
        for {
          v <- escape(transfer(obj))
          _ <- modify(_.copyObj(v.loc)(loc))
        } yield AbsValue(loc)
      }
      case keys @ EKeys(mobj, intSorted) => {
        val loc: AllocSite = AllocSite(keys.asite, cp.view)
        for {
          v <- escape(transfer(mobj))
          _ <- modify(_.keys(v.loc, intSorted)(loc))
        } yield AbsValue(loc)
      }
      case ENotSupported(msg) => AbsValue.Bot
    }

    // return if abrupt completion
    def returnIfAbrupt(
      value: AbsValue,
      check: Boolean
    ): Result[AbsValue] = {
      val comp = value.comp
      val checkReturn: Result[Unit] =
        if (check) doReturn(AbsValue(comp = comp.removeNormal))
        else ()
      val newValue = comp.normal.value ⊔ value.pure
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
    ): AbsValue = operand.simple.getSingle match {
      case FlatBot => AbsValue.Bot
      case FlatElem(ASimple(x)) =>
        AbsValue(Interp.interp(uop, x))
      case FlatTop => uop match {
        case ONeg => ???
        case ONot => ???
        case OBNot => ???
      }
    }

    // binary operators
    def transfer(
      st: AbsState,
      bop: BOp,
      left: AbsValue,
      right: AbsValue
    ): AbsValue = (left.getSingle, right.getSingle) match {
      case (FlatBot, _) | (_, FlatBot) => AbsValue.Bot
      case (FlatElem(ASimple(l)), FlatElem(ASimple(r))) =>
        AbsValue(Interp.interp(bop, l, r))
      case (FlatElem(l), FlatElem(r)) if bop == OEq || bop == OEqual =>
        (l, r) match {
          case (lloc: Loc, rloc: Loc) => if (lloc == rloc) {
            if (st.isSingle(lloc)) AVT
            else AVB
          } else AVF
          case _ => AbsValue(l == r)
        }
      case _ => bop match {
        case OAnd => ???
        case OBAnd => ???
        case OBOr => ???
        case OBXOr => ???
        case ODiv => ???
        case OEq => ???
        case OEqual => ???
        case OLShift => ???
        case OLt => ???
        case OMod => ???
        case OMul => ???
        case OOr => ???
        case OPlus => ???
        case OPow => ???
        case OSRShift => ???
        case OSub => ???
        case OUMod => ???
        case OURShift => ???
        case OXor => ???
      }
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
      b = l.bool
      v <- (bop, b.getSingle) match {
        case (OAnd, FlatElem(Bool(false))) => pure(AVF)
        case (OOr, FlatElem(Bool(true))) => pure(AVT)
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
    import AbsObj._
    Map(
      "GetArgument" -> {
        case List(v) => id(_.pop(v.loc, AbsValue(0)))
      },
      // TODO fix bug
      "IsDuplicate" -> {
        case List(v) => for {
          st <- get
        } yield AbsValue(bool = v.loc.foldLeft(AbsBool.Bot: AbsBool) {
          case (b, loc) => b ⊔ (st(loc) match {
            case ListElem(vs) if vs.forall(_.isSingle) => AbsBool(Bool((for {
              v <- vs
              av <- v.getSingle match {
                case FlatElem(av) => Some(av)
                case _ => None
              }
            } yield av).toSet.size != vs.length))
            case _ => AB
          })
        })
      },
      "IsArrayIndex" -> {
        case List(v) => v.getSingle match {
          case FlatBot => AbsValue.Bot
          case FlatElem(sv) => sv match {
            case ASimple(Str(s)) =>
              val d = ESValueParser.str2num(s)
              val ds = toStringHelper(d)
              val UPPER = (1L << 32) - 1
              val l = d.toLong
              AbsValue(ds == s && 0 <= l && d == l && l < UPPER)
            case _ => AVF
          }
          case FlatTop => ???
        }
      },
      "min" -> {
        case List(v0, v1) => (v0.getSingle, v1.getSingle) match {
          case (_, FlatBot) | (FlatBot, _) => AbsValue.Bot
          case (FlatElem(ASimple(n0: Numeric)), FlatElem(ASimple(n1: Numeric))) =>
            AbsValue(n0.min(n1))
          case _ => ???
        }
      },
      "max" -> {
        case List(v0, v1) => (v0.getSingle, v1.getSingle) match {
          case (_, FlatBot) | (FlatBot, _) => AbsValue.Bot
          case (FlatElem(ASimple(n0: Numeric)), FlatElem(ASimple(n1: Numeric))) =>
            AbsValue(n0.max(n1))
          case _ => ???
        }
      },
      "abs" -> {
        case List(v) => v.getSingle match {
          case FlatBot => AbsValue.Bot
          case FlatElem(ASimple(n: Numeric)) => AbsValue(n.abs)
          case _ => ???
        }
      },
      "floor" -> {
        case List(v) => v.getSingle match {
          case FlatBot => AbsValue.Bot
          case FlatElem(ASimple(n: Numeric)) => AbsValue(n.floor)
          case _ => ???
        }
      },
      "fround" -> { case args => ??? },
      "ThrowCompletion" -> {
        case List(value) => value.wrapCompletion("throw")
      },
      "NormalCompletion" -> {
        case List(value) => pure(value.wrapCompletion)
      },
      "IsAbruptCompletion" -> {
        case List(value) => pure(AbsValue(bool = value.isAbruptCompletion))
      },
    )
  }
}
