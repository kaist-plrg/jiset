package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.{ CFG_DIR, LOG }
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.analyzer
import kr.ac.kaist.jiset.spec.algorithm.SyntaxDirectedHead
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import scala.Console._
import scala.annotation.tailrec

// abstract transfer function
class AbsTransfer(
  val sem: AbsSemantics,
  usePrune: Boolean = false,
  replMode: Boolean = false
) {
  import sem.cfg._
  analyzer.transfer = this

  // result of abstract transfer
  val monad = new StateMonad[AbsState]
  import monad._

  // worklist
  val worklist = sem.worklist

  // stats
  val stat = sem.stat

  // repl
  val REPL = new AnalyzeREPL(sem)

  // bottom checker
  val checkBottoms = new CheckBottoms(sem)

  // fixpoint computation
  @tailrec
  final def compute: Unit = worklist.next match {
    case Some(cp) =>
      // alarm for weirdly-bottom'ed vars and objects
      try {
        if (replMode) REPL.run(cp)
        apply(cp)
      } catch {
        case e: Throwable =>
          if (LOG) stat.dump()
          printlnColor(RED)(s"[Error] An exception is thrown.")
          println(sem.getString(cp, CYAN, true))
          dumpCFG(sem, Some(cp), depth = Some(5))
          throw e
      }
      stat.iter += 1
      if (LOG && stat.iter % 10000 == 0) stat.dump()
      compute
    case None =>
      sem.noReturnCheck
      if (LOG) stat.dump()
      stat.close()
      nfAlarms.close()
      nfErrors.close()
  }

  // transfer function for control points
  def apply(cp: ControlPoint): Unit = {
    alarmCP = cp
    alarmCPStr = sem.getString(cp, "", false)
    checkBottoms(cp)
    cp match {
      case (np: NodePoint[_]) => this(np)
      case (rp: ReturnPoint) => this(rp)
    }
  }

  // transfer function for node points
  def apply[T <: Node](np: NodePoint[T]): Unit = {
    val st = sem(np)
    val NodePoint(node, view) = np
    val func = funcOf(node)
    val helper = new Helper(ReturnPoint(func, view))

    import helper._
    node match {
      case (entry: Entry) =>
        val newSt = handleThis(func, st)
        sem += NodePoint(next(entry), view) -> newSt
      case (exit: Exit) => alarm("may be no return")
      case (block: Block) =>
        val newSt = join(block.insts.map(transfer))(st)
        sem += NodePoint(next(block), view) -> newSt
      case (call: Call) =>
        val newSt = transfer(call, view)(st)
        sem += NodePoint(next(call), view) -> newSt
      case branch @ Branch(_, expr) =>
        val (_, newSt) = transfer(expr)(st)
        sem += NodePoint(thenNext(branch), view) -> newSt
        sem += NodePoint(elseNext(branch), view) -> newSt
    }
  }

  // handle this value for syntax-directed algorithms
  def handleThis(func: Function, st: AbsState): AbsState = func.algo.head match {
    case (head: SyntaxDirectedHead) =>
      val lhsName = head.lhsName
      if (head.params.map(_.name) contains lhsName) st
      else st + (lhsName -> st("this"))
    case _ => st
  }

  // transfer function for return points
  def apply(rp: ReturnPoint): Unit = {
    val newT = sem(rp)
    for ((np @ NodePoint(call, view), x) <- sem.getRetEdges(rp)) {
      val nextNP = np.copy(node = next(call))
      val newSt = sem(np) + (x -> newT)
      sem += nextNP -> newSt
    }
  }

  private class Helper(ret: ReturnPoint) {
    // function
    val func = ret.func
    val fid = func.uid

    // transfer function for normal instructions
    def transfer(inst: NormalInst): Updater = ???
    // def transfer(inst: NormalInst): Updater = inst match {
    //   case IExpr(expr @ ENotSupported(msg)) => st => {
    //     alarm(expr.beautified)
    //     st
    //   }
    //   case IExpr(expr) => transfer(expr)
    //   case ILet(Id(x), expr) => for {
    //     v <- transfer(expr)
    //     _ <- modify(_ + (x -> v))
    //   } yield ()
    //   case IAssign(ref, expr) => for {
    //     refv <- transfer(ref)
    //     v <- transfer(expr)
    //     _ <- modify(_.update(sem, refv, v))
    //   } yield ()
    //   case IDelete(ref) => for {
    //     refv <- transfer(ref)
    //     _ <- modify(_.delete(sem, refv))
    //   } yield ()
    //   case IAppend(expr, list) => for {
    //     v <- transfer(expr)
    //     l <- transfer(list)
    //     _ <- modify(_.append(sem, v.escaped, l.escaped.loc))
    //   } yield ()
    //   case IPrepend(expr, list) => for {
    //     v <- transfer(expr)
    //     l <- transfer(list)
    //     _ <- modify(_.prepend(sem, v.escaped, l.escaped.loc))
    //   } yield ()
    //   case IReturn(expr) => for {
    //     v <- transfer(expr)
    //     st <- get
    //     _ <- put(AbsState.Bot)
    //   } yield sem.doReturn(ret -> (st.heap, v.toCompletion))
    //   case ithrow @ IThrow(x) => for {
    //     st <- get
    //     comp = AbsComp(CompThrow -> ((AbsType(NamedAddr(x)), emptyConst)))
    //     _ <- put(AbsState.Bot)
    //   } yield sem.doReturn(ret -> ((st.heap, comp)))
    //   case IAssert(expr) if usePrune => for {
    //     pv <- pruneTransfer(expr)
    //     _ <- modify(pv.pruneT)
    //   } yield assert(pv.v, expr)
    //   case IAssert(expr) => for {
    //     v <- transfer(expr)
    //   } yield assert(v, expr)
    //   case IPrint(expr) => for {
    //     v <- transfer(expr)
    //     _ = printlnColor(GREEN)(s"[PRINT] ${beautify(v)}")
    //   } yield ()
    //   case IWithCont(id, params, bodyInst) => st => {
    //     alarm(s"not yet implemented: ${inst.beautified}")
    //     st
    //   }
    //   case ISetType(expr, ty) => for {
    //     v <- transfer(expr)
    //     p = v.escaped
    //   } yield {
    //     alarm(s"not yet implemented: ${inst.beautified}")
    //   }
    // }

    // transfer function for call instructions
    def transfer(call: Call, view: View): Updater = ???
    // def transfer(call: Call, view: View): Updater = call.inst match {
    //   case IApp(Id(x), ERef(RefId(Id(name))), List(arg)) if unaryAlgos contains name => for {
    //     v <- transfer(arg)
    //     ty <- get(unaryAlgos(name)(_, v.escaped))
    //     _ <- modify(_ + (x -> ty))
    //   } yield ()
    //   case IApp(Id(x), fexpr, args) => for {
    //     f <- transfer(fexpr)
    //     vs <- join(args.map(arg => transfer(arg)))
    //     st <- get
    //     _ <- put(AbsState.Bot)
    //   } yield sem.doCall(call, view, st, f.escaped.clo, vs, x)
    //   case IAccess(Id(x), bexpr, expr, args) => for {
    //     b <- transfer(bexpr)
    //     p <- transfer(expr)
    //     vs <- join(args.map(arg => transfer(arg)))
    //     st <- get
    //     v = access(call, view, x, b.escaped, p.escaped.str, vs, st)
    //     _ <- {
    //       if (v.isBottom) put(AbsState.Bot)
    //       else modify(_ + (x -> v))
    //     }
    //   } yield ()
    // }

    // unary algorithms
    type UnaryAlgo = (AbsState, AbsType) => AbsType
    val unaryAlgos: Map[String, UnaryAlgo] = ???
    // val unaryAlgos: Map[String, UnaryAlgo] = Map(
    //   "IsDuplicate" -> ((st, v) => AbsBool.Top),
    //   "IsArrayIndex" -> ((st, v) => AbsBool.Top),
    //   "ThrowCompletion" -> ((st, v) => {
    //     AbsComp(CompThrow -> (v.escaped, emptyConst))
    //   }),
    //   "NormalCompletion" -> ((st, v) => v.toCompletion),
    //   "IsAbruptCompletion" -> ((st, v) => {
    //     var res: AbsBool = AbsBool.Bot
    //     if (!v.comp.abrupt.isBottom) res ⊔= AT
    //     if (!v.comp.normal.isBottom) res ⊔= AF
    //     if (!v.pure.isBottom) res ⊔= AF
    //     res
    //   }),
    //   "floor" -> ((st, v) => AbsNum.Top),
    //   "abs" -> ((st, v) => AbsNum.Top),
    // )

    // transfer function for expressions
    // TODO consider the completion records
    def transfer(expr: Expr): Result[AbsType] = ???
    // def transfer(expr: Expr): Result[AbsType] = expr match {
    //   case ENum(n) => AbsType(n)
    //   case EINum(n) => AbsType(n)
    //   case EBigINum(b) => AbsType(b)
    //   case EStr(str) => AbsType(str)
    //   case EBool(b) => AbsType(b)
    //   case EUndef => AbsType(Undef)
    //   case ENull => AbsType(Null)
    //   case EAbsent => AbsType(Absent)
    //   case expr @ EMap(Ty(ty), props) => for {
    //     vs <- join(props.map {
    //       case (kexpr, vexpr) => for {
    //         v <- transfer(vexpr)
    //         k = kexpr.to[EStr](???).str
    //       } yield k -> v
    //     })
    //     asite = expr.asite
    //     a <- id(_.allocMap(fid, asite, ty, vs.toMap))
    //   } yield a
    //   case expr @ EList(exprs) => for {
    //     vs <- join(exprs.map(transfer))
    //     a <- id(_.allocList(fid, expr.asite, vs.toList))
    //   } yield a
    //   case expr @ ESymbol(desc) => for {
    //     // TODO handling non-string descriptions
    //     a <- id(_.allocSymbol(fid, expr.asite, desc.to[EStr](???).str))
    //   } yield a
    //   case EPop(list, idx) => for {
    //     l <- transfer(list)
    //     k <- transfer(idx)
    //     a <- id(_.pop(sem, l.escaped, k.escaped))
    //   } yield a
    //   case ERef(ref) => for {
    //     refv <- transfer(ref)
    //     v <- get(_.lookup(sem, refv))
    //   } yield v
    //   // TODO after discussing the continuations
    //   case ECont(params, body) => st => {
    //     alarm(s"not yet implemented: ${expr.beautified}")
    //     (AbsType.Bot, st)
    //   }
    //   case EUOp(ONot, EBOp(OEq, ERef(ref), EAbsent)) => isAbsent(ref, true)
    //   case EBOp(OEq, ERef(ref), EAbsent) => isAbsent(ref)
    //   case EUOp(uop, expr) => for {
    //     v <- transfer(expr)
    //     u = transfer(uop)(v.escaped)
    //   } yield u
    //   case EBOp(bop, left, right) => for {
    //     l <- transfer(left)
    //     r <- transfer(right)
    //     v = transfer(bop)(l.escaped, r.escaped)
    //   } yield v
    //   case ETypeOf(expr) => for {
    //     v <- transfer(expr)
    //     set <- get(_.typeOf(sem, v.escaped))
    //   } yield AbsStr(set)
    //   case EIsCompletion(expr) => for {
    //     v <- transfer(expr)
    //   } yield {
    //     var res = AbsType.Bot
    //     if (!v.comp.isBottom) res ⊔= AT
    //     if (!v.pure.isBottom) res ⊔= AF
    //     res
    //   }
    //   case EIsInstanceOf(base, name) => for {
    //     v <- transfer(base)
    //   } yield boolTop // TODO more precise
    //   case EGetElems(base, name) => for {
    //     v <- transfer(expr)
    //     p = v.escaped
    //   } yield {
    //     alarm(s"not yet implemented: ${expr.beautified}")
    //     AbsType.Bot
    //   } // TODO need discussion
    //   case EGetSyntax(base) => strTop // TODO handling non-AST values
    //   case EParseSyntax(code, rule, flags) => for {
    //     r <- transfer(rule)
    //     ast = r.escaped.str.gamma match {
    //       case Infinite => AbsAST.Top
    //       case Finite(set) => AbsAST.alpha(set.map(str => ASTVal(str.str)))
    //     }
    //   } yield ast
    //   case EConvert(source, cop, flags) => for {
    //     v <- transfer(source)
    //   } yield cop match {
    //     case CStrToNum => AbsNum.Top
    //     case CStrToBigInt => AbsBigINum.Top
    //     case CNumToStr => AbsStr.Top
    //     case CNumToInt => AbsNum.Top
    //     case CNumToBigInt => AbsBigINum.Top
    //     case CBigIntToNum => AbsNum.Top
    //   }
    //   case EContains(list, elem) => for {
    //     l <- transfer(list)
    //     e <- transfer(elem)
    //     c <- get(_.contains(l.escaped, e.escaped))
    //   } yield c
    //   case EReturnIfAbrupt(ERef(ref), check) => for {
    //     rv <- transfer(ref)
    //     v <- transfer(rv)
    //     newV <- returnIfAbrupt(v, check)
    //     _ <- {
    //       if (newV.isBottom) put(AbsState.Bot)
    //       else modify(_.update(sem, rv, newV))
    //     }
    //   } yield newV
    //   case EReturnIfAbrupt(expr, check) => for {
    //     v <- transfer(expr)
    //     newV <- returnIfAbrupt(v, check)
    //     _ <- {
    //       if (newV.isBottom) put(AbsState.Bot)
    //       else pure(())
    //     }
    //   } yield newV
    //   case expr @ ECopy(obj) => for {
    //     v <- transfer(obj)
    //     a <- id(_.copyOf(sem, fid, expr.asite, v.escaped))
    //   } yield a
    //   case EKeys(obj) => for {
    //     v <- transfer(obj)
    //     a <- id(_.keysOf(v.escaped))
    //   } yield a
    //   case expr @ ENotSupported(msg) => st => {
    //     alarm(expr.beautified)
    //     (AbsType(Absent), st)
    //   }
    // }

    // transfer function for reference values
    def transfer(ref: Ref): Result[AbsRef] = ???
    // def transfer(ref: Ref): Result[AbsRef] = ref match {
    //   case RefId(id) => AbsRef.Id(id.name)
    //   case RefProp(ref, expr) => for {
    //     rv <- transfer(ref)
    //     b <- transfer(rv)
    //     p <- transfer(expr)
    //     r <- AbsRef.Prop(b, p.escaped)
    //   } yield r // TODO handle non-string properties
    // }

    // transfer function for reference values
    def transfer(refv: AbsRef): Result[AbsType] = ???
    // def transfer(refv: AbsRef): Result[AbsType] =
    //   st => (st.lookup(sem, refv), st)

    // transfer function for unary operators
    def transfer(uop: UOp): AbsType => AbsType = ???
    // def transfer(uop: UOp): AbsType => AbsType = v => uop match {
    //   case ONeg => numericTop
    //   case ONot => !v.escaped.bool
    //   case OBNot => numTop
    // }

    // transfer function for binary operators
    def transfer(bop: BOp): (AbsType, AbsType) => AbsType = ???
    // def transfer(bop: BOp): (AbsType, AbsType) => AbsType = (l, r) => bop match {
    //   case OPlus => arithBOp(l, r)
    //   case OSub => arithBOp(l, r)
    //   case OMul => arithBOp(l, r)
    //   case OPow => numericBOp(l, r)
    //   case ODiv => numericBOp(l, r)
    //   case OUMod => numericBOp(l, r)
    //   case OMod => numericBOp(l, r)
    //   case OLt => boolTop
    //   case OEq => l =^= r
    //   case OEqual => boolTop
    //   case OAnd => l.bool && r.bool
    //   case OOr => l.bool || r.bool
    //   case OXor => l.bool ^ r.bool
    //   case OBAnd => numTop
    //   case OBOr => numTop
    //   case OBXOr => numTop
    //   case OLShift => numTop
    //   case OSRShift => numTop
    //   case OURShift => numTop
    // }

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

    // // alarm if assertion fails
    // def assert(v: AbsType, expr: Expr) =
    //   if (!(AT ⊑ v.escaped.bool)) alarm(s"assertion failed: ${expr.beautified}")

    // // access semantics
    // def access(
    //   call: Call,
    //   view: View,
    //   x: String,
    //   value: AbsType,
    //   prop: AbsStr,
    //   args: List[AbsType],
    //   st: AbsState
    // ): AbsType = {
    //   var v = AbsType.Bot

    //   // AST cases
    //   for {
    //     ASTVal(ast) <- value.ast.gamma
    //     Str(name) <- prop.gamma
    //   } v ⊔= accessAST(call, view, x, ast, name, args, st)

    //   // reference cases
    //   v ⊔= st.lookup(sem, AbsRef.Prop(value, prop))

    //   v
    // }

    // // access of AST values
    // def accessAST(
    //   call: Call,
    //   view: View,
    //   x: String,
    //   ast: String,
    //   name: String,
    //   args: List[AbsType],
    //   st: AbsState
    // ): AbsType = if (sem.spec.getRhsNT(ast) contains name) AbsType.Bot
    // else (ast, name) match {
    //   case ("IdentifierName", "StringValue") => strTop
    //   case ("NumericLiteral", "NumericValue") => numTop
    //   case ("StringLiteral", "StringValue" | "SV") => strTop
    //   case (_, "TV" | "TRV") => strTop
    //   case (_, "MV") => numTop
    //   case _ =>
    //     val fids = getSyntaxFids(ast, name)
    //     if (fids.isEmpty) {
    //       if (name == "Contains") AbsBool.Top
    //       else {
    //         alarm(s"$ast.$name does not exist")
    //         AbsType.Bot
    //       }
    //     } else {
    //       val pairs = fids.toList.flatMap[AbsClo.Pair](fid => {
    //         val func = fidMap(fid)
    //         func.algo.head match {
    //           case (head: SyntaxDirectedHead) =>
    //             val baseArgs = sem.getArgs(head)
    //             sem.doCall(call, view, st, AbsClo(Clo(fid)), baseArgs ++ args, x)
    //             None
    //           case _ => None
    //         }
    //       })
    //       val v: AbsType = AbsClo(AbsClo.SetD(pairs: _*))
    //       v
    //     }
    // }

    // // all numbers
    // private val numTop: AbsType = AbsNum.Top

    // // all big integers
    // private val bigintTop: AbsType = AbsBigINum.Top

    // // all numeric values
    // private val numericTop: AbsType = AbsPrim(
    //   num = AbsNum.Top,
    //   bigint = AbsBigINum.Top,
    // )

    // // all strings
    // private val strTop: AbsType = AbsStr.Top

    // // all arithmetic values
    // private val arithTop: AbsType = AbsPrim(
    //   num = AbsNum.Top,
    //   bigint = AbsBigINum.Top,
    //   str = AbsStr.Top,
    // )
  }
}
