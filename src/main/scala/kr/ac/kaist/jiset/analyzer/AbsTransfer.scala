package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.{ CFG_DIR, LOG }
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.spec.algorithm.SyntaxDirectedHead
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import scala.Console._
import scala.annotation.tailrec

// abstract transfer function
object AbsTransfer {
  // result of abstract transfer
  import AbsState.monad._

  // abstract semantics
  val sem = AbsSemantics

  // initialize type infos
  Type.infos

  // fixpoint computation
  @tailrec
  final def compute: Unit = worklist.next match {
    case Some(cp) =>
      // alarm for weirdly-bottom'ed vars and objects
      try {
        if (REPL) AnalyzeREPL.run(cp)
        apply(cp)
      } catch {
        case e: Throwable =>
          if (e.getMessage != "stop for debugging") {
            if (LOG) Stat.dump()
            printlnColor(RED)(s"[Error] An exception is thrown.")
            println(sem.getString(cp, CYAN, true))
            dumpCFG(Some(cp), depth = Some(5))
          }
          throw e
      }
      Stat.iter += 1
      if (LOG && Stat.iter % 10000 == 0) Stat.dump()
      compute
    case None =>
      alarmCP = null
      alarmCPStr = ""
      sem.noReturnCheck
      sem.referenceCheck
      if (DOT) dumpCFG(None, PDF)
      if (REPL) {
        printlnColor(RED)(s"* Analysis finished.")
        AnalyzeREPL.runDirect(alarmCP)
      }
      if (LOG) Stat.dump()
      Stat.close()
      nfAlarms.close()
      nfErrors.close()
  }

  // transfer function for control points
  def apply(cp: ControlPoint): Unit = {
    alarmCP = cp
    alarmCPStr = sem.getString(cp, "", false)
    CheckBottoms(cp)
    cp match {
      case (np: NodePoint[_]) => this(np)
      case (rp: ReturnPoint) => this(rp)
    }
  }

  // transfer function for node points
  def apply[T <: Node](np: NodePoint[T]): Unit = {
    val st = sem(np)
    val NodePoint(node, view) = np
    val func = cfg.funcOf(node)
    val ret = ReturnPoint(func, view)
    val helper = new Helper(ret)

    import helper._
    node match {
      case (entry: Entry) =>
        val newSt = handleThis(func, st)
        sem += NodePoint(cfg.next(entry), view) -> newSt
      case (exit: Exit) =>
        sem.doReturn(ret, Undef)
      case (block: Block) =>
        val newSt = join(block.insts.map(transfer))(st)
        sem += NodePoint(cfg.next(block), view) -> newSt
      case (call: Call) =>
        val newSt = transfer(call, view)(st)
        sem += NodePoint(cfg.next(call), view) -> newSt
      case branch @ Branch(_, expr) =>
        val (t, newSt) = transfer(expr)(st)
        val cond = t.escaped(expr)
        if (AT ⊑ cond) {
          val np = NodePoint(cfg.thenNext(branch), view)
          sem += np -> prune(st, expr, true)(newSt)
        }
        if (AF ⊑ cond) {
          val np = NodePoint(cfg.elseNext(branch), view)
          sem += np -> prune(st, expr, false)(newSt)
        }
    }
  }

  // handle this value for syntax-directed algorithms
  def handleThis(func: Function, st: AbsState): AbsState = func.algo.head match {
    case (head: SyntaxDirectedHead) =>
      val lhsName = head.lhsName
      if (head.params.map(_.name) contains lhsName) st
      else st.define(lhsName, st.lookupVar("this"))
    case _ => st
  }

  // transfer function for return points
  def apply(rp: ReturnPoint): Unit = {
    val newT = sem(rp)
    for ((np @ NodePoint(call, view), x) <- sem.getRetEdges(rp)) {
      val nextNP = np.copy(node = cfg.next(call))
      val newSt = sem(np).define(x, newT)
      sem += nextNP -> newSt
    }
  }

  class Helper(ret: ReturnPoint) extends PruneHelper {
    // function
    val func = ret.func
    val fid = func.uid

    // transfer function for normal instructions
    def transfer(inst: NormalInst): Updater = bottomCheck((inst match {
      case IExpr(expr @ ENotSupported(msg)) => st => {
        warning(expr.beautified)
        st
      }
      case IExpr(expr) => for {
        t <- transfer(expr)
        _ <- {
          if (t.isBottom) put(AbsState.Bot)
          else pure(())
        }
      } yield ()
      case ILet(Id(x), expr) => for {
        t <- transfer(expr)
        _ <- modify(_.define(x, t, check = true))
      } yield ()
      case IAssign(ref, expr) => for {
        r <- transfer(ref)
        t <- transfer(expr)
        rexpr = ERef(ref)
        _ <- modify(_.update(rexpr, r, ref match {
          case _: RefId => t
          case _: RefProp => t.escaped(rexpr)
        }))
      } yield ()
      case IDelete(ref) => for {
        r <- transfer(ref)
        _ <- modify(_.delete(r))
      } yield ()
      case IAppend(expr, list) => for {
        v <- transfer(expr)
        l <- transfer(list)
        _ <- modify(_.append(v, l))
      } yield ()
      case IPrepend(expr, list) => for {
        v <- transfer(expr)
        l <- transfer(list)
        _ <- modify(_.prepend(v, l))
      } yield ()
      case IReturn(expr) => for {
        t <- transfer(expr)
        _ <- put(AbsState.Bot)
      } yield sem.doReturn(ret, t.noAbsent.toComp)
      case ithrow @ IThrow(x) => for {
        _ <- put(AbsState.Bot)
      } yield sem.doReturn(ret, AbruptT)
      case IAssert(expr) => for {
        st <- get
        t <- transfer(expr)
        _ <- modify(prune(st, expr, true))
        tv = t.escaped(expr)
        _ <- if (tv ⊑ AF) put(AbsState.Bot) else pure(())
      } yield assert(tv, expr)
      case IPrint(expr) => for {
        t <- transfer(expr)
        _ = printlnColor(GREEN)(s"[PRINT] $t")
      } yield ()
      case _ => st => {
        warning(s"not yet implemented: ${inst.beautified}")
        st
      }
    }): Updater)

    // transfer function for call instructions
    def transfer(call: Call, view: View): Updater = call.inst match {
      case IApp(Id(x), ERef(RefId(Id(name))), args) if predAlgos contains name => for {
        as <- join(args.map(transfer))
        ty <- get(predAlgos(name)(_, args zip as))
        _ <- modify(_.define(x, ty))
      } yield ()
      case IApp(Id(x), fexpr, args) => for {
        f <- transfer(fexpr)
        as <- join(args.map(transfer))
        _ <- put(AbsState.Bot)
        fids = f.fidSet
      } yield if (fids.isEmpty) warning("no function") else fids.foreach(fid => {
        val func = cfg.fidMap(fid)
        sem.doCall(call, view, func, as, x)
      })
      case IAccess(Id(x), bexpr, EStr(prop), args) => for {
        b <- transfer(bexpr)
        ts <- join(args.map(arg => transfer(arg)))
        st <- get
        // TODO
        rexpr = ERef(RefProp(toRef(bexpr.beautified), EStr(prop)))
        t = b.escapedSet(bexpr)
          .map(access(rexpr, call, view, x, _, prop, ts, st))
          .foldLeft(AbsType.Bot)(_ ⊔ _)
        _ <- {
          if (t.isBottom) put(AbsState.Bot)
          else modify(_.define(x, t))
        }
      } yield ()
      case inst => st => {
        warning(s"not yet implemented: ${inst.beautified}")
        st
      }
    }

    // check bottom abstract types
    def bottomCheck(f: Updater): Updater = st => if (st.isBottom) st else f(st)
    def bottomCheck(f: Result[AbsType]): Result[AbsType] = bottomCheck(f, "")
    def bottomCheck(f: Result[AbsType], expr: Expr, target: Any): Result[AbsType] =
      expr match {
        case EReturnIfAbrupt(_, true) => f
        case _ => bottomCheck(f, target)
      }
    def bottomCheck(f: Result[AbsType], target: Any): Result[AbsType] =
      for (t <- f) yield { bottomCheck(t, target); t }
    def bottomCheck(t: AbsType): Boolean = bottomCheck(t, "")
    def bottomCheck(t: AbsType, target: Any): Boolean = if (t.isBottom) {
      warning("bottom result" + (if (target == "") target else s" @ $target"))
      true
    } else false

    // unary algorithms
    type PredAlgo = (AbsState, List[(Expr, AbsType)]) => AbsType
    def arityCheck(name: String, f: PredAlgo): (String, PredAlgo) =
      (name, (st, pairs) => optional(f(st, pairs)).getOrElse {
        alarm(s"arity mismatch for $name")
        AbsType.Bot
      })
    val predAlgos: Map[String, PredAlgo] = Map(
      arityCheck("IsDuplicate", { case (_, List(_)) => BoolT }),
      arityCheck("IsArrayIndex", { case (_, List(_)) => BoolT }),
      arityCheck("ThrowCompletion", { case (_, List(_)) => AbruptT }),
      arityCheck("NormalCompletion", { case (_, List((_, ty))) => ty.toComp }),
      arityCheck("IsAbruptCompletion", { case (_, List((_, ty))) => ty =^= AbruptT }),
      arityCheck("floor", { case (_, List(_)) => NumT }),
      arityCheck("abs", { case (_, List(_)) => NumT }),
      arityCheck("fround", {
        case (_, List((expr, ty))) =>
          if (!(ty ⊑ NumT)) alarm(s"non-number types: ${expr.beautified}")
          NumT
      }),
      arityCheck("min", { case (_, _) => NumT }),
      arityCheck("max", { case (_, _) => NumT }),
    )

    // integer post-fix pattern
    val intPostFix = "(\\D*)(\\d+)".r

    // transfer function for expressions
    def transfer(expr: Expr): Result[AbsType] = bottomCheck(expr match {
      case ExistCheck(x) => x
      case ShortCircuit(x) => x
      case ENum(n) => Num(n).abs
      case EINum(n) => Num(n).abs
      case EBigINum(b) => BigInt(b).abs
      case EStr(str) => Str(str).abs
      case EBool(b) => Bool(b).abs
      case EUndef => Undef.abs
      case ENull => Null.abs
      case EAbsent => Absent.abs
      case EMap(Ty("Completion"), props) =>
        val map = props.toMap
        (map.get(EStr("Type")), map.get(EStr("Value"))) match {
          case (Some(ERef(RefId(Id("CONST_normal")))), Some(e)) => for {
            t <- transfer(e)
          } yield AbsType(t.escapedSet(e).map(NormalT(_): Type))
          case _ => pure(AbruptT.abs)
        }
      case EMap(Ty("Record"), props) => for {
        ps <- join(props.collect {
          case (EStr(prop), expr) => for {
            t <- transfer(expr)
          } yield prop -> t.escaped(expr).upcast
        })
      } yield RecordT(ps.toMap)
      case EMap(Ty(name), props) => for {
        ps <- join(props.collect {
          case (EStr(prop), expr) => for {
            t <- transfer(expr)
          } yield prop -> t.escaped(expr).upcast
        })
      } yield {
        // get type name
        val verboseName = name + "Record"
        val tyProps = ps.toMap
        val ty =
          if ((Type.infoMap contains verboseName) &&
            !(Type.infoMap contains name)) verboseName
          else name

        // check props
        Type.propMap.get(ty) match {
          case Some(pmap) => tyProps.foreach {
            case (prop, _) if prop == "SubMap" => // ignore SubMap
            case (prop, propT) if pmap contains prop =>
              if ((propT ⊓ pmap(prop)).isBottom) {
                warning(s"invalid property type: ${pmap(prop)} is expected at ${ty}.${prop}(current: ${propT})")
              }
            case (prop, _) => warning(s"unknown property: ${ty}.${prop}")
          }
          case None if ty != "SubMap" => warning(s"unknown type: ${ty}")
          case _ =>
        }

        NameT(ty).abs
      }
      case EList(exprs) => for {
        ts <- join(exprs.map(transfer))
        set = ts.foldLeft(AbsType.Bot)(_ ⊔ _).noAbsent.escapedSet(expr)
      } yield (set.size match {
        case 0 => NilT
        case 1 => ListT(set.head.upcast)
        case _ => ListT(set.head.upcast)
      })
      case ESymbol(desc) => SymbolT.abs
      case EPop(list, idx) => for {
        l <- transfer(list)
        k <- transfer(idx)
        a <- id(_.pop(l.escaped(list), k.escaped(idx)))
      } yield a
      case ERef(ref) => for {
        r <- transfer(ref)
        t <- get(_.lookup(expr, r))
      } yield t
      case EUOp(uop, expr) => for {
        v <- transfer(expr)
        t = transfer(uop, expr)(v.escaped(expr))
      } yield t
      case EBOp(bop, left, right) => for {
        l <- transfer(left)
        r <- transfer(right)
        t = transfer(bop, left, right)(l.escaped(left), r.escaped(right))
      } yield t
      case ETypeOf(expr) => for {
        v <- transfer(expr)
        t <- get(_.typeof(v.escaped(expr)))
      } yield t
      case EIsCompletion(expr) => for {
        t <- transfer(expr)
      } yield AbsType(t.set.map[Type] {
        case NormalT(_) | AbruptT => T
        case _ => F
      })
      case EIsInstanceOf(base, intPostFix(name, kStr)) => for {
        v <- transfer(base)
        t <- get(_.isInstanceOf(v.escaped(base), name, kStr.toInt))
      } yield t
      case EIsInstanceOf(base, name) => for {
        v <- transfer(base)
        t <- get(_.isInstanceOf(v.escaped(base), name))
      } yield t
      case EGetSyntax(base) => for {
        b <- transfer(base)
      } yield b.escaped(base).getSingle match {
        case Some(Str("BooleanLiteral")) => AbsType(Str("true"), Str("false"))
        case _ => StrT.abs
      }
      case EParseSyntax(code, EStr(rule), flags) => AstT(rule).abs
      case EConvert(source, cop, flags) => for {
        t <- transfer(source)
      } yield cop match {
        case CStrToNum => NumT
        case CStrToBigInt => BigIntT
        case CNumToStr => StrT
        case CNumToInt => NumT
        case CNumToBigInt => BigIntT
        case CBigIntToNum => NumT
      }
      case EContains(list, elem) => for {
        l <- transfer(list)
        e <- transfer(elem)
        c <- get(_.contains(l, e))
      } yield c
      case EReturnIfAbrupt(rexpr @ ERef(ref), check) => for {
        r <- transfer(ref)
        t <- transfer(expr, r)
        newT = returnIfAbrupt(t, check)
        _ <- {
          if (newT.isBottom) put(AbsState.Bot)
          else modify(_.update(rexpr, r, newT))
        }
      } yield newT
      case EReturnIfAbrupt(expr, check) => for {
        t <- transfer(expr)
        newT = returnIfAbrupt(t, check)
        _ <- {
          if (newT.isBottom) put(AbsState.Bot)
          else pure(())
        }
      } yield newT
      case expr @ ECopy(obj) => for {
        t <- transfer(obj)
      } yield AbsType(t.escaped(obj).set.filter {
        case NameT(_) | ESValueT | NilT | ListT(_) | MapT(_) => true
        case _ => false
      })
      case EKeys(obj) => for {
        t <- transfer(obj)
      } yield ListT(StrT)
      case ENotSupported(msg) => st => {
        warning(expr.beautified)
        (Absent, st)
      }
      case expr => st => {
        warning(s"not yet implemented: ${expr.beautified}")
        (Absent, st)
      }
    }, expr, expr.beautified)

    // existence check
    object ExistCheck {
      def unapply(expr: Expr): Option[Result[AbsType]] = optional(expr match {
        case EUOp(ONot, EBOp(OEq, rexpr @ ERef(ref), EAbsent)) => for {
          r <- transfer(ref)
          b <- get(_.exists(rexpr, r))
        } yield b
        case EBOp(OEq, rexpr @ ERef(ref), EAbsent) => for {
          r <- transfer(ref)
          b <- get(_.exists(rexpr, r))
        } yield !b
        case _ => error("not existence check")
      })
    }

    // short circuit
    object ShortCircuit {
      def unapply(expr: Expr): Option[Result[AbsType]] = optional(expr match {
        case EBOp(OOr, left, right) => for {
          l <- transfer(left)
          le = l.escaped(left)
          r <- if (le == AT) pure(AT) else transfer(right)
          re = r.escaped(right)
        } yield l || r
        case EBOp(OAnd, left, right) => for {
          l <- transfer(left)
          le = l.escaped(left)
          r <- if (le == AF) pure(AF) else transfer(right)
          re = r.escaped(right)
        } yield l && r
        case _ => error("not existence check")
      })
    }

    // transfer function for reference values
    def transfer(ref: Ref): Result[AbsRef] = ref match {
      case RefId(id) => AbsId(id.name)
      case RefProp(base, EStr(str)) => for {
        r <- transfer(base)
        b <- transfer(ERef(base), r)
      } yield AbsStrProp(b, str)
      case RefProp(ref, expr) => for {
        rv <- transfer(ref)
        b <- transfer(ERef(ref), rv)
        p <- transfer(expr)
      } yield AbsGeneralProp(b, p)
    }

    // transfer function for reference values
    def transfer(rexpr: Expr, ref: AbsRef): Result[AbsType] = bottomCheck(st => {
      (st.lookup(rexpr, ref), st)
    }, ref)

    // transfer function for unary operators
    def transfer(uop: UOp, expr: Expr): AbsType => AbsType = t => {
      if (bottomCheck(t)) AbsType.Bot
      else uop match {
        case ONeg => -t
        case ONot => !t
        case OBNot => NumT
      }
    }

    // transfer function for binary operators
    def transfer(
      bop: BOp,
      left: Expr,
      right: Expr
    ): (AbsType, AbsType) => AbsType = (l, r) => {
      if (bottomCheck(l) || bottomCheck(r)) AbsType.Bot
      else bop match {
        case OPlus => arithBOp(l, r)
        case OSub => arithBOp(l, r)
        case OMul => arithBOp(l, r)
        case OPow => numericBOp(l, r)
        case ODiv => numericBOp(l, r)
        case OUMod => numericBOp(l, r)
        case OMod => numericBOp(l, r)
        case OLt => (l.getSingle, r.getSingle) match {
          case (Some(Num(l)), Some(Num(r))) => Bool(l < r)
          case _ =>
            if (!(l ⊑ NumericT))
              alarm(s"non-numeric types: ${left.beautified}")
            if (!(r ⊑ NumericT))
              alarm(s"non-numeric types: ${right.beautified}")
            BoolT
        }
        case OEq => l =^= r
        case OEqual => BoolT
        case OAnd => l && r
        case OOr => l || r
        case OXor => l ^ r
        case OBAnd => NumT
        case OBOr => NumT
        case OBXOr => NumT
        case OLShift => NumT
        case OSRShift => NumT
        case OURShift => NumT
      }
    }
    private def arithBOp(l: AbsType, r: AbsType): AbsType =
      if (bottomCheck(l) || bottomCheck(r)) AbsType.Bot
      else if (l ⊑ StrT && r ⊑ StrT) StrT
      else if (l ⊑ NumT && r ⊑ NumT) NumT
      else if (l ⊑ BigIntT && r ⊑ BigIntT) BigIntT
      else if (l ⊑ NumericT && r ⊑ NumericT) NumericT
      else ArithT
    private def numericBOp(l: AbsType, r: AbsType): AbsType =
      if (bottomCheck(l) || bottomCheck(r)) AbsType.Bot
      else if (l ⊑ NumT && r ⊑ NumT) NumT
      else if (l ⊑ BigIntT && r ⊑ BigIntT) BigIntT
      else NumericT

    // return if abrupt completion
    def returnIfAbrupt(t: AbsType, check: Boolean): AbsType = {
      AbsType(t.set.flatMap[Type] {
        case AbruptT =>
          if (check) sem.doReturn(ret, AbruptT)
          else warning(s"Unchecked abrupt completions")
          None
        case NormalT(t) => Some(t)
        case (t: PureType) => Some(t)
      })
    }

    // alarm if assertion fails
    def assert(t: AbsType, expr: Expr) = {
      if (!(AT ⊑ t)) alarm(s"assertion failed: ${expr.beautified}")
      else if (AF ⊑ t) warning(s"maybe assertion failed: ${expr.beautified}")
    }

    // access semantics
    def access(
      expr: Expr,
      call: Call,
      view: View,
      x: String,
      base: PureType,
      prop: String,
      args: List[AbsType],
      st: AbsState
    ): AbsType = base match {
      case AstT(name) => accessAST(call, view, x, name, prop, args)
      case _ => st.lookup(expr, AbsStrProp(base, prop))
    }

    // access of AST values
    def accessAST(
      call: Call,
      view: View,
      x: String,
      name: String,
      prop: String,
      args: List[AbsType]
    ): AbsType = (name, prop) match {
      case ("IdentifierName", "StringValue") => StrT
      case ("NumericLiteral", "NumericValue") => NumT
      case ("StringLiteral", "StringValue" | "SV") => StrT
      case (_, "TV" | "TRV") => StrT
      case (_, "MV") => NumT
      case (_, prop) if cfg.spec.grammar.nameMap contains prop => AstT(prop)
      case _ => cfg.getSyntaxFids(name, prop).toList match {
        case Nil if prop == "Contains" => BoolT
        case Nil =>
          warning(s"$name.$prop does not exist")
          AbsType.Bot
        case fids =>
          fids.foreach(fid => {
            val func = cfg.fidMap(fid)
            func.algo.head match {
              case (head: SyntaxDirectedHead) =>
                val baseArgs = sem.getArgs(head)
                sem.doCall(call, view, func, baseArgs ++ args, x)
              case _ =>
            }
          })
          AbsType.Bot
      }
    }
  }
}
