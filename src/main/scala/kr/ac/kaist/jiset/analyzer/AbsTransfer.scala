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
  val monad = new StateMonad[AbsState]
  import monad._

  // abstract semantics
  val sem = AbsSemantics

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
          if (LOG) Stat.dump()
          printlnColor(RED)(s"[Error] An exception is thrown.")
          println(sem.getString(cp, CYAN, true))
          dumpCFG(Some(cp), depth = Some(5))
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
        val (_, newSt) = transfer(expr)(st)
        sem += NodePoint(cfg.thenNext(branch), view) -> newSt
        sem += NodePoint(cfg.elseNext(branch), view) -> newSt
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

  private class Helper(ret: ReturnPoint) {
    // function
    val func = ret.func
    val fid = func.uid

    // transfer function for normal instructions
    def transfer(inst: NormalInst): Updater = inst match {
      case IExpr(expr @ ENotSupported(msg)) => st => {
        alarm(expr.beautified)
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
      // case IDelete(ref) => for {
      //   refv <- transfer(ref)
      //   _ <- modify(_.delete(sem, refv))
      // } yield ()
      case IAppend(expr, list) => for {
        v <- transfer(expr)
        l <- transfer(list)
        _ <- modify(_.append(v, l))
      } yield ()
      // case IPrepend(expr, list) => for {
      //   v <- transfer(expr)
      //   l <- transfer(list)
      //   _ <- modify(_.prepend(sem, v.escaped, l.escaped.loc))
      // } yield ()
      case IReturn(expr) => for {
        ty <- transfer(expr)
        _ <- put(AbsState.Bot)
      } yield sem.doReturn(ret, ty.toComp)
      // case ithrow @ IThrow(x) => for {
      //   st <- get
      //   comp = AbsComp(CompThrow -> ((AbsType(NamedAddr(x)), emptyConst)))
      //   _ <- put(AbsState.Bot)
      // } yield sem.doReturn(ret -> ((st.heap, comp)))
      case IAssert(expr) => for {
        t <- transfer(expr)
      } yield assert(t, expr)
      // case IPrint(expr) => for {
      //   v <- transfer(expr)
      //   _ = printlnColor(GREEN)(s"[PRINT] ${beautify(v)}")
      // } yield ()
      // case IWithCont(id, params, bodyInst) => st => {
      //   alarm(s"not yet implemented: ${inst.beautified}")
      //   st
      // }
      // case ISetType(expr, ty) => for {
      //   v <- transfer(expr)
      //   p = v.escaped
      // } yield {
      //   alarm(s"not yet implemented: ${inst.beautified}")
      // }
      case _ => ???
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
      } yield f.set.foreach {
        case CloT(fid) =>
          val func = cfg.fidMap(fid)
          sem.doCall(call, view, func, as, x)
        case _ => alarm("no function")
      }
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
      case _ => ???
    }

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

    // transfer function for expressions
    def transfer(expr: Expr): Result[AbsType] = expr match {
      case ENum(n) => Num(n).abs
      case EINum(n) => Num(n).abs
      case EBigINum(b) => BigInt(b).abs
      case EStr(str) => Str(str).abs
      case EBool(b) => Bool(b).abs
      case EUndef => Undef.abs
      case ENull => Null.abs
      case EAbsent => Absent.abs
      // case expr @ EMap(Ty(ty), props) => for {
      //   vs <- join(props.map {
      //     case (kexpr, vexpr) => for {
      //       v <- transfer(vexpr)
      //       k = kexpr.to[EStr](???).str
      //     } yield k -> v
      //   })
      //   asite = expr.asite
      //   a <- id(_.allocMap(fid, asite, ty, vs.toMap))
      // } yield a
      case EList(exprs) => for {
        ts <- join(exprs.map(transfer))
        set = ts.foldLeft(AbsType.Bot)(_ ⊔ _).escapedSet
      } yield (set.size match {
        case 0 => NilT
        case 1 => ListT(set.head)
        case _ => ListT(set.head)
      })
      // case expr @ ESymbol(desc) => for {
      //   // TODO handling non-string descriptions
      //   a <- id(_.allocSymbol(fid, expr.asite, desc.to[EStr](???).str))
      // } yield a
      // case EPop(list, idx) => for {
      //   l <- transfer(list)
      //   k <- transfer(idx)
      //   a <- id(_.pop(sem, l.escaped, k.escaped))
      // } yield a
      case ERef(ref) => for {
        r <- transfer(ref)
        t <- get(_.lookup(r))
      } yield t
      // case EUOp(ONot, EBOp(OEq, ERef(ref), EAbsent)) => isAbsent(ref, true)
      // case EBOp(OEq, ERef(ref), EAbsent) => isAbsent(ref)
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
      // case EIsCompletion(expr) => for {
      //   v <- transfer(expr)
      // } yield {
      //   var res = AbsType.Bot
      //   if (!v.comp.isBottom) res ⊔= AT
      //   if (!v.pure.isBottom) res ⊔= AF
      //   res
      // }
      // case EIsInstanceOf(base, name) => for {
      //   v <- transfer(base)
      // } yield BoolT // TODO more precise
      // case EGetSyntax(base) => StrT // TODO handling non-AST values
      case EParseSyntax(code, EStr(rule), flags) => AstT(rule).abs
      // case EConvert(source, cop, flags) => for {
      //   v <- transfer(source)
      // } yield cop match {
      //   case CStrToNum => AbsNum.Top
      //   case CStrToBigInt => AbsBigINum.Top
      //   case CNumToStr => AbsStr.Top
      //   case CNumToInt => AbsNum.Top
      //   case CNumToBigInt => AbsBigINum.Top
      //   case CBigIntToNum => AbsNum.Top
      // }
      case EContains(list, elem) => for {
        l <- transfer(list)
        e <- transfer(elem)
        c <- get(_.contains(l, e))
      } yield c
      // case EReturnIfAbrupt(ERef(ref), check) => for {
      //   rv <- transfer(ref)
      //   v <- transfer(rv)
      //   newV <- returnIfAbrupt(v, check)
      //   _ <- {
      //     if (newV.isBottom) put(AbsState.Bot)
      //     else modify(_.update(sem, rv, newV))
      //   }
      // } yield newV
      // case EReturnIfAbrupt(expr, check) => for {
      //   v <- transfer(expr)
      //   newV <- returnIfAbrupt(v, check)
      //   _ <- {
      //     if (newV.isBottom) put(AbsState.Bot)
      //     else pure(())
      //   }
      // } yield newV
      // case expr @ ECopy(obj) => for {
      //   v <- transfer(obj)
      //   a <- id(_.copyOf(sem, fid, expr.asite, v.escaped))
      // } yield a
      // case EKeys(obj) => for {
      //   v <- transfer(obj)
      //   a <- id(_.keysOf(v.escaped))
      // } yield a
      // case expr @ ENotSupported(msg) => st => {
      //   alarm(expr.beautified)
      //   (AbsType(Absent), st)
      // }
      case _ => ???
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
    def transfer(uop: UOp): AbsType => AbsType = t => uop match {
      case ONeg if t ⊑ NumT => NumT
      case ONeg if t ⊑ BigIntT => BigIntT
      case ONeg => AbsType(NumT, BigIntT)
      case ONot if t.set == Set(Bool(true)) => Bool(false)
      case ONot if t.set == Set(Bool(false)) => Bool(true)
      case ONot => BoolT
      case OBNot => NumT
    }

    // transfer function for binary operators
    def transfer(bop: BOp): (AbsType, AbsType) => AbsType = (l, r) => bop match {
      case OPlus => arithBOp(l, r)
      case OSub => arithBOp(l, r)
      case OMul => arithBOp(l, r)
      case OPow => numericBOp(l, r)
      case ODiv => numericBOp(l, r)
      case OUMod => numericBOp(l, r)
      case OMod => numericBOp(l, r)
      case OLt => BoolT
      case OEq => BoolT
      case OEqual => BoolT
      case OAnd => BoolT
      case OOr => BoolT
      case OXor => BoolT
      case OBAnd => NumT
      case OBOr => NumT
      case OBXOr => NumT
      case OLShift => NumT
      case OSRShift => NumT
      case OURShift => NumT
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

    // predefined values
    val NumericT = AbsType(NumT, BigIntT)
    val ArithT = AbsType(NumT, BigIntT, StrT)

    // // return if abrupt completion
    // def returnIfAbrupt(v: AbsType, check: Boolean): Result[AbsType] = st => {
    //   val AbsType(pure, comp) = v
    //   val compV: AbsType = comp.isNormal.map {
    //     case true => comp(CompNormal)._1
    //     case false =>
    //       val abrupt = comp.abrupt
    //       if (check) sem.doReturn(ret -> (st.heap, abrupt))
    //       else alarm(s"Unchecked abrupt completions: ${beautify(abrupt)}")
    //       AbsType.Bot
    //   }.foldLeft(AbsType.Bot)(_ ⊔ _)
    //   val newV: AbsType = pure ⊔ compV
    //   (newV, st)
    // }

    // alarm if assertion fails
    def assert(v: AbsType, expr: Expr) = {
      if (!(Bool(true) ⊑ v)) alarm(s"assertion failed: ${expr.beautified}")
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
      case _ =>
        val fids = cfg.getSyntaxFids(name, prop)
        if (fids.isEmpty) if (prop == "Contains") BoolT else {
          alarm(s"$name.$prop does not exist")
        }
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
