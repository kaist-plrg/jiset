package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.CFG_DIR
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec

// abstract transfer function
class AbsTransfer(sem: AbsSemantics, var interactMode: Boolean = false) {
  import sem.cfg._

  // worklist
  val worklist = sem.worklist

  // fixpoint computation
  @tailrec
  final def compute: Unit = worklist.headOption match {
    case Some(cp) =>
      if (interactMode) interact(cp)
      worklist.next
      apply(cp)
      compute
    case None =>
  }

  // result of abstract transfer
  val monad = new StateMonad[AbsState]
  import monad._

  // transfer function for control points
  def apply(cp: ControlPoint): Unit = cp match {
    case (np: NodePoint) => this(np)
    case (rp: ReturnPoint) => this(rp)
  }

  // transfer function for node points
  def apply(np: NodePoint): Unit = {
    val st = sem(np)
    val NodePoint(node, view) = np
    val helper = new Helper(ReturnPoint(funcOf(node), view))
    import helper._
    node match {
      case (entry: Entry) =>
        sem += NodePoint(next(entry), view) -> st
      case (exit: Exit) => // TODO detect missing return
      case (block: Block) =>
        val newSt = join(block.insts.map(transfer))(st)
        sem += NodePoint(next(block), view) -> newSt
      case call @ Call(inst) =>
        val newSt = transfer(inst)(st)
        sem += NodePoint(next(call), view) -> newSt
      case branch @ Branch(expr) =>
        val (v, newSt) = transfer(expr)(st)
        v.bool.toSet.foreach {
          case true =>
            sem += NodePoint(thenNext(branch), view) -> prune(expr, true)(st)
          case false =>
            sem += NodePoint(elseNext(branch), view) -> prune(expr, false)(st)
        }
    }
  }

  // transfer function for return points
  def apply(rp: ReturnPoint): Unit = {
    // TODO handle inter-procedural cases
  }

  // interactive mode
  def interact(cp: ControlPoint): Unit = {
    val dot = (new DotPrinter)(cp, sem).toString
    dumpFile(dot, s"$CFG_DIR.dot")
    executeCmd(s"""dot -Tpdf "$CFG_DIR.dot" -o "$CFG_DIR.pdf"""")
    println(sem.getString(cp))
    println
    val str = scala.io.StdIn.readLine()
    str match {
      case null | "q" | "quit" | "exit" => interactMode = false
      case _ =>
    }
  }

  private class Helper(ret: ReturnPoint) {
    // transfer function for normal instructions
    def transfer(inst: NormalInst): Updater = inst match {
      case IExpr(expr) => transfer(expr)
      case ILet(Id(x), expr) => for {
        v <- transfer(expr)
        _ <- modify(_ + (x -> v))
      } yield ()
      case IAssign(ref, expr) => for {
        refv <- transfer(ref)
        v <- transfer(refv)
        _ <- modify(_.update(sem, refv, v))
      } yield ()
      case IDelete(ref) => for {
        refv <- transfer(ref)
        _ <- modify(_.delete(sem, refv))
      } yield ()
      case IAppend(expr, list) => for {
        v <- transfer(expr)
        l <- transfer(list)
        _ <- modify(_.append(v, l.addr))
      } yield ()
      case IPrepend(expr, list) => for {
        v <- transfer(expr)
        l <- transfer(list)
        _ <- modify(_.prepend(v, l.addr))
      } yield ()
      case IReturn(expr) => for {
        v <- transfer(expr)
        st <- get
        _ <- put(AbsState.Bot)
      } yield sem.doReturn(ret -> (st.heap, v))
      case IThrow(id) => ???
      case IAssert(expr) => ???
      case IPrint(expr) => ???
      case IWithCont(id, params, bodyInst) => ???
      case ISetType(expr, ty) => ???
      case _ => ???
    }

    // transfer function for call instructions
    def transfer(inst: CallInst): Updater = inst match {
      case IApp(Id(x), fexpr, args) => for {
        f <- transfer(fexpr)
        vs <- join(args.map(arg => transfer(arg)))
        st <- get
        _ <- put(AbsState.Bot)
      } yield for (ts <- getTypes(st, vs)) {
        val view = View(ts)
        f.clo.foreach {
          case AbsClo.Pair(fid, _) =>
            val func = fidMap(fid)
            println("---- call ----")
            println(s"func: ${func.name}")
            val args = func.algo.params.map(_.name) zip vs
            println(s"args: ${args.mkString("[", ", ", "]")}")
            val np = NodePoint(func.entry, view)
            println(s"np: $np")
            val newSt = args.foldLeft(st.copy(env = AbsEnv.Empty)) {
              case (st, (x, v)) => st + (x -> v)
            }
            sem += np -> newSt
        }
      }
      case IAccess(Id(x), bexpr, expr) => for {
        b <- transfer(bexpr)
        p <- transfer(expr)
        v = (b.getSingle, p.getSingle) match {
          case (One(ASTVal(ast)), One(Str(name))) => (ast, name) match {
            case ("NumericLiteral", "NumericValue") => numTop
            case ("StringLiteral", "StringValue" | "SV") => strTop
            case _ => ???
          }
          case _ => ???
        }
        _ <- modify(_ + (x -> v)) // TODO handling call cases
      } yield ()
    }

    // transfer function for expressions
    // TODO consider the completion records
    def transfer(expr: Expr): Result[AbsValue] = expr match {
      case ENum(n) => AbsValue(n)
      case EINum(n) => AbsValue(n)
      case EBigINum(b) => AbsValue(b)
      case EStr(str) => AbsValue(str)
      case EBool(b) => AbsValue(b)
      case EUndef => AbsValue(Undef)
      case ENull => AbsValue(Null)
      case EAbsent => AbsValue(Absent)
      case EMap(ty, props) => for {
        vs <- join(props.map {
          case (kexpr, vexpr) => for {
            v <- transfer(expr)
            k = kexpr.to[EStr](???).str
          } yield k -> v
        })
        // TODO handling type information
        a <- id(_.allocMap(vs.toMap))
      } yield a
      case EList(exprs) => for {
        vs <- join(exprs.map(transfer))
        a <- id(_.allocList(vs.toList))
      } yield a
      case ESymbol(desc) =>
        // TODO handling non-string descriptions
        _.allocSymbol(desc.to[EStr](???).str)
      case EPop(list, idx) => for {
        l <- transfer(list)
        k <- transfer(idx)
        a <- id(_.pop(l, k))
      } yield a
      case ERef(ref) => for {
        refv <- transfer(ref)
        v <- get(_(sem, refv))
      } yield v
      // TODO after discussing the continuations
      case ECont(params, body) => ???
      case EUOp(uop, expr) => for {
        v <- transfer(expr)
        u = transfer(uop)(v)
      } yield u
      case EBOp(bop, left, right) => for {
        l <- transfer(left)
        r <- transfer(right)
        v = transfer(bop)(l, r)
      } yield v
      case ETypeOf(expr) => for {
        v <- transfer(expr)
        t <- get(_.typeOf(v))
      } yield t
      case EIsCompletion(expr) => for {
        v <- transfer(expr)
      } yield ??? // TODO after discussing the completion structures
      case EIsInstanceOf(base, name) => for {
        v <- transfer(expr)
      } yield ??? // TODO need discussion
      case EGetElems(base, name) => for {
        v <- transfer(expr)
      } yield ??? // TODO need discussion
      case EGetSyntax(base) => strTop // TODO handling non-AST values
      case EParseSyntax(code, rule, flags) => for {
        c <- transfer(code)
        r <- transfer(rule)
        // XXX maybe flags are not necessary in abstract semantics
      } yield ???
      case EConvert(source, target, flags) => for {
        v <- transfer(source)
      } yield ??? // TODO need discussion
      case EContains(list, elem) => for {
        l <- transfer(list)
        e <- transfer(elem)
        c <- get(_.contains(l, e))
      } yield c
      case EReturnIfAbrupt(expr, check) =>
        transfer(expr) // TODO support abrupt completion check
      case ECopy(obj) => for {
        v <- transfer(obj)
        a <- id(_.copyOf(v))
      } yield a
      case EKeys(obj) => for {
        v <- transfer(obj)
        a <- id(_.keysOf(v))
      } yield a
      case ENotSupported(msg) => ??? // TODO need discussion
    }

    // transfer function for reference values
    def transfer(ref: Ref): Result[AbsRefValue] = ref match {
      case RefId(id) => AbsRefValue.Id(id.name)
      case RefProp(ref, expr) => for {
        rv <- transfer(ref)
        b <- transfer(rv)
        p <- transfer(expr)
        r <- AbsRefValue(b, p.str)
      } yield r // TODO handle non-string properties
    }

    // transfer function for reference values
    def transfer(refv: AbsRefValue): Result[AbsValue] = {
      println(beautify(refv))
      ???
    }

    // transfer function for unary operators
    // TODO more precise abstract semantics
    def transfer(uop: UOp): AbsValue => AbsValue = v => uop match {
      case ONeg => numTop
      case ONot => !v.bool
      case OBNot => intTop
    }

    // all booleans
    val boolTop: AbsValue = AbsBool.Top

    // transfer function for binary operators
    // TODO more precise abstract semantics
    def transfer(bop: BOp): (AbsValue, AbsValue) => AbsValue = (l, r) => bop match {
      case OPlus => arithTop
      case OSub => arithTop
      case OMul => arithTop
      case OPow => numTop
      case ODiv => numTop
      case OUMod => numTop
      case OMod => numTop
      case OLt => boolTop
      case OEq => boolTop
      case OEqual => boolTop
      case OAnd => l.bool && r.bool
      case OOr => l.bool || r.bool
      case OXor => l.bool ^ r.bool
      case OBAnd => intTop
      case OBOr => intTop
      case OBXOr => intTop
      case OLShift => intTop
      case OSRShift => intTop
      case OURShift => AbsINum.Top
    }

    // TODO pruning abstract states using conditions
    def prune(expr: Expr, cond: Boolean): Updater = st => st

    // all integers
    private val intTop: AbsValue = AbsPrim(
      int = AbsINum.Top,
      bigint = AbsBigINum.Top
    )

    // all numbers
    private val numTop: AbsValue = AbsPrim(
      num = AbsNum.Top,
      int = AbsINum.Top,
      bigint = AbsBigINum.Top
    )

    // all strings
    private val strTop: AbsValue = AbsStr.Top

    // all arithmetic values
    private val arithTop: AbsValue = AbsPrim(
      num = AbsNum.Top,
      int = AbsINum.Top,
      bigint = AbsBigINum.Top,
      str = AbsStr.Top
    )

    // get types from abstract values
    private def getTypes(st: AbsState, vs: List[AbsValue]): List[List[Type]] = {
      vs.foldRight(List(List[Type]())) {
        case (v, tysList) => for {
          tys <- tysList
          ty <- getType(st, v)
        } yield ty :: tys
      }
    }
    private def getType(st: AbsState, v: AbsValue): List[Type] = ???
  }
}
