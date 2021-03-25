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
      sem.noReturnCheck
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
    val helper = new Helper(ReturnPoint(func, view))

    import helper._
    node match {
      case (entry: Entry) =>
        val newSt = handleThis(func, st)
        sem += NodePoint(cfg.next(entry), view) -> newSt
      case (exit: Exit) => alarm("may be no return")
      case (block: Block) =>
        val newSt = join(block.insts.map(transfer))(st)
        sem += NodePoint(cfg.next(block), view) -> newSt
      case (call: Call) =>
        val newSt = transfer(call, view)(st)
        sem += NodePoint(cfg.next(call), view) -> newSt
      case branch @ Branch(_, expr) =>
        val (t, newSt) = transfer(expr)(st)
        if (AT ⊑ t) {
          val np = NodePoint(cfg.thenNext(branch), view)
          sem += np -> prune(st, expr, true)(newSt)
        }
        if (AF ⊑ t) {
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
    def transfer(inst: NormalInst): Updater = inst match {
      case IExpr(expr @ ENotSupported(msg)) => st => {
        warning(expr.beautified)
        st
      }
      case IExpr(expr) => transfer(expr)
      case ILet(Id(x), expr) => for {
        t <- transfer(expr)
        _ <- modify(_.define(x, t))
      } yield ()
      case IAssign(ref, expr) => for {
        r <- transfer(ref)
        t <- transfer(expr)
        _ <- modify(_.update(r, t))
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
      } yield sem.doReturn(ret, t.toComp)
      case ithrow @ IThrow(x) => for {
        _ <- put(AbsState.Bot)
      } yield AbruptT
      case IAssert(expr) => for {
        st <- get
        t <- transfer(expr)
        _ = modify(prune(st, expr, true))
      } yield assert(t, expr)
      case IPrint(expr) => for {
        t <- transfer(expr)
        _ = printlnColor(GREEN)(s"[PRINT] $t")
      } yield ()
      case _ => st => {
        alarm(s"not yet implemented: ${inst.beautified}")
        st
      }
    }

    // transfer function for call instructions
    def transfer(call: Call, view: View): Updater = call.inst match {
      case IApp(Id(x), ERef(RefId(Id(name))), List(arg)) if unaryAlgos contains name => for {
        a <- transfer(arg)
        ty <- get(unaryAlgos(name)(_, a))
        _ <- modify(_.define(x, ty))
      } yield ()
      case IApp(Id(x), fexpr, args) => for {
        f <- transfer(fexpr)
        as <- join(args.map(arg => transfer(arg)))
        _ <- put(AbsState.Bot)
        fids = f.fidSet
      } yield if (fids.isEmpty) alarm("no function") else fids.foreach(fid => {
        val func = cfg.fidMap(fid)
        sem.doCall(call, view, func, as, x)
      })
      case IAccess(Id(x), bexpr, EStr(prop), args) => for {
        b <- transfer(bexpr)
        ts <- join(args.map(arg => transfer(arg)))
        st <- get
        t = b.escapedSet
          .map(access(call, view, x, _, prop, ts, st))
          .foldLeft(AbsType.Bot)(_ ⊔ _)
        _ <- {
          if (t.isBottom) put(AbsState.Bot)
          else modify(_.define(x, t))
        }
      } yield ()
      case inst => st => {
        alarm(s"not yet implemented: ${inst.beautified}")
        st
      }
    }

    def bottomCheck(t: AbsType): Result[Unit] =
      if (t.isBottom) put(AbsState.Bot) else ()

    // unary algorithms
    type UnaryAlgo = (AbsState, AbsType) => AbsType
    val unaryAlgos: Map[String, UnaryAlgo] = Map(
      "IsDuplicate" -> ((st, ty) => BoolT),
      "IsArrayIndex" -> ((st, ty) => BoolT),
      "ThrowCompletion" -> ((st, ty) => AbruptT),
      "NormalCompletion" -> ((st, ty) => ty.toComp),
      "IsAbruptCompletion" -> ((st, ty) => BoolT),
      "floor" -> ((st, ty) => NumT),
      "abs" -> ((st, ty) => NumT),
    )

    // integer post-fix pattern
    val intPostFix = "(\\D*)(\\d+)".r

    // transfer function for expressions
    def transfer(expr: Expr): Result[AbsType] = expr match {
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
      case EMap(Ty(name), props) => NameT(name).abs
      case EList(exprs) => for {
        ts <- join(exprs.map(transfer))
        set = ts.foldLeft(AbsType.Bot)(_ ⊔ _).escapedSet
      } yield (set.size match {
        case 0 => NilT
        case 1 => ListT(set.head.upcast)
        case _ => ListT(set.head.upcast)
      })
      case ESymbol(desc) => SymbolT.abs
      case EPop(list, idx) => for {
        l <- transfer(list)
        k <- transfer(idx)
        a <- id(_.pop(l.escaped, k.escaped))
      } yield a
      case ERef(ref) => for {
        r <- transfer(ref)
        t <- get(_.lookup(r))
      } yield t
      case EUOp(uop, expr) => for {
        v <- transfer(expr)
        t = transfer(uop)(v.escaped)
      } yield t
      case EBOp(bop, left, right) => for {
        l <- transfer(left)
        r <- transfer(right)
        t = transfer(bop)(l.escaped, r.escaped)
      } yield t
      case ETypeOf(expr) => for {
        v <- transfer(expr)
        t <- get(_.typeof(v.escaped))
      } yield t
      case EIsCompletion(expr) => for {
        t <- transfer(expr)
      } yield AbsType(t.set.map[Type] {
        case NormalT(_) | AbruptT => T
        case _ => F
      })
      case EIsInstanceOf(base, intPostFix(name, kStr)) => for {
        v <- transfer(base)
        t <- get(_.isInstanceOf(v.escaped, name, kStr.toInt))
      } yield t
      case EIsInstanceOf(base, name) => for {
        v <- transfer(base)
        t <- get(_.isInstanceOf(v.escaped, name))
      } yield t
      case EGetSyntax(base) => StrT.abs
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
      case EReturnIfAbrupt(expr, check) => for {
        t <- transfer(expr)
        newT = returnIfAbrupt(t, check)
        _ <- {
          if (newT.isBottom) put(AbsState.Bot)
          else ()
        }
      } yield newT
      case expr @ ECopy(obj) => for {
        t <- transfer(obj)
      } yield AbsType(t.escaped.set.filter {
        case NameT(_) => true
        case ESValueT => true
        case ListT(_) => true
        case MapT(_) => true
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
        alarm(s"not yet implemented: ${expr.beautified}")
        (Absent, st)
      }
    }

    // existence check
    object ExistCheck {
      def unapply(expr: Expr): Option[Result[AbsType]] = optional(expr match {
        case EUOp(ONot, EBOp(OEq, ERef(ref), EAbsent)) => for {
          r <- transfer(ref)
          t <- get(_.lookup(r, check = false))
        } yield !t.isAbsent
        case EBOp(OEq, ERef(ref), EAbsent) => for {
          r <- transfer(ref)
          t <- get(_.lookup(r, check = false))
        } yield t.isAbsent
        case _ => error("not existence check")
      })
    }

    // short circuit
    object ShortCircuit {
      def unapply(expr: Expr): Option[Result[AbsType]] = optional(expr match {
        case EBOp(OOr, left, right) => for {
          l <- transfer(left)
          le = l.escaped
          r <- if (le == AT) pure(AT) else transfer(right)
          re = r.escaped
        } yield l || r
        case EBOp(OAnd, left, right) => for {
          l <- transfer(left)
          le = l.escaped
          r <- if (le == AF) pure(AF) else transfer(right)
          re = r.escaped
        } yield l && r
        case _ => error("not existence check")
      })
    }

    // transfer function for reference values
    def transfer(ref: Ref): Result[AbsRef] = ref match {
      case RefId(id) => AbsId(id.name)
      case RefProp(base, EStr(str)) => for {
        r <- transfer(base)
        b <- transfer(r)
      } yield AbsStrProp(b, str)
      case RefProp(ref, expr) => for {
        rv <- transfer(ref)
        b <- transfer(rv)
        p <- transfer(expr)
      } yield AbsGeneralProp(b, p)
    }

    // transfer function for reference values
    def transfer(ref: AbsRef): Result[AbsType] = st => {
      (st.lookup(ref), st)
    }

    // transfer function for unary operators
    def transfer(uop: UOp): AbsType => AbsType = t => {
      if (t.isBottom) AbsType.Bot
      else uop match {
        case ONeg => -t
        case ONot => !t
        case OBNot => NumT
      }
    }

    // transfer function for binary operators
    def transfer(bop: BOp): (AbsType, AbsType) => AbsType = (l, r) => {
      if (l.isBottom || r.isBottom) AbsType.Bot
      else bop match {
        case OPlus => arithBOp(l, r)
        case OSub => arithBOp(l, r)
        case OMul => arithBOp(l, r)
        case OPow => numericBOp(l, r)
        case ODiv => numericBOp(l, r)
        case OUMod => numericBOp(l, r)
        case OMod => numericBOp(l, r)
        case OLt => BoolT
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
      if (l.isBottom || r.isBottom) AbsType.Bot
      else if (l ⊑ StrT && r ⊑ StrT) StrT
      else if (l ⊑ NumT && r ⊑ NumT) NumT
      else if (l ⊑ BigIntT && r ⊑ BigIntT) BigIntT
      else if (l ⊑ NumericT && r ⊑ NumericT) NumericT
      else ArithT
    private def numericBOp(l: AbsType, r: AbsType): AbsType =
      if (l.isBottom || r.isBottom) AbsType.Bot
      else if (l ⊑ NumT && r ⊑ NumT) NumT
      else if (l ⊑ BigIntT && r ⊑ BigIntT) BigIntT
      else NumericT

    // return if abrupt completion
    def returnIfAbrupt(t: AbsType, check: Boolean): AbsType = {
      AbsType(t.set.flatMap[Type] {
        case AbruptT =>
          if (check) sem.doReturn(ret, AbruptT)
          else alarm(s"Unchecked abrupt completions")
          None
        case NormalT(t) => Some(t)
        case (t: PureType) => Some(t)
      })
    }

    // alarm if assertion fails
    def assert(t: AbsType, expr: Expr) = {
      if (!(AT ⊑ t)) alarm(s"assertion failed: ${expr.beautified}")
    }

    // access semantics
    def access(
      call: Call,
      view: View,
      x: String,
      base: PureType,
      prop: String,
      args: List[AbsType],
      st: AbsState
    ): AbsType = base match {
      case AstT(name) => accessAST(call, view, x, name, prop, args)
      case _ => st.lookup(AbsStrProp(base, prop))
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
      case (_, "Contains") => BoolT
      case (_, prop) if cfg.spec.grammar.nameMap contains prop => AstT(prop)
      case _ =>
        val fids = cfg.getSyntaxFids(name, prop)
        if (fids.isEmpty) alarm(s"$name.$prop does not exist")
        else fids.foreach(fid => {
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
