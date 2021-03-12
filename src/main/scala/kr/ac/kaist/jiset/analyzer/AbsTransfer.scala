package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.{ CFG_DIR, LOG, LINE_SEP }
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.analyzer
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.spec.algorithm.SyntaxDirectedHead
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import org.jline.builtins.Completers.TreeCompleter
import org.jline.builtins.Completers.TreeCompleter.{ Node => CNode, node }
import org.jline.reader._
import org.jline.reader.impl._
import org.jline.terminal._
import org.jline.utils.InfoCmp.Capability
import org.jline.utils._
import scala.util.matching.Regex
import scala.collection.mutable.ArrayBuffer
import scala.Console._
import scala.annotation.tailrec

// abstract transfer function
class AbsTransfer(
  sem: AbsSemantics,
  usePrune: Boolean = true,
  replMode: Boolean = false
) {
  import sem.cfg._
  analyzer.transfer = this

  // result of abstract transfer
  val monad = new StateMonad[AbsState]
  import monad._

  // manual semantics
  val model = sem.model

  // worklist
  val worklist = sem.worklist

  // stats
  val stat = sem.stat

  // fixpoint computation
  @tailrec
  final def compute: Unit = worklist.next match {
    case Some(cp) =>
      try {
        if (replMode) REPL.run(cp)
        apply(cp)
      } catch {
        case e: Throwable =>
          printlnColor(RED)(s"[Error] An exception is thrown.")
          println(sem.getString(cp, CYAN, true))
          dumpCFG(Some(cp), depth = Some(5))
          throw e
      }
      stat.iter += 1
      if (LOG && stat.iter % 10000 == 0) stat.dump()
      compute
    case None =>
      if (LOG) stat.dump()
      stat.close()
      nfAlarms.close()
  }

  // transfer function for control points
  def apply(cp: ControlPoint): Unit = {
    alarmCP = cp
    alarmCPStr = sem.getString(cp, "", false)
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

    // transfer branch
    def transferBranch(
      branch: Branch,
      v: AbsValue,
      newSt: AbsState,
      pruneT: Updater,
      pruneF: Updater
    ): Unit = v.escaped.bool.toSet.foreach {
      case true =>
        sem += NodePoint(thenNext(branch), view) -> pruneT(newSt)
      case false =>
        sem += NodePoint(elseNext(branch), view) -> pruneF(newSt)
    }

    import helper._
    node match {
      case (entry: Entry) =>
        val newSt = handleThisValue(func, st)
        sem += NodePoint(next(entry), view) -> newSt
      case (exit: Exit) => // TODO detect missing return
      case (block: Block) =>
        val newSt = join(block.insts.map(transfer))(st)
        sem += NodePoint(next(block), view) -> newSt
      case (call: Call) =>
        val newSt = transfer(call, view)(st)
        sem += NodePoint(next(call), view) -> newSt
      case branch @ Branch(_, expr) if usePrune =>
        val (pv, newSt) = pruneTransfer(expr)(st)
        transferBranch(branch, pv.v, newSt, pv.pruneT, pv.pruneF)
      case branch @ Branch(_, expr) =>
        val (v, newSt) = transfer(expr)(st)
        val ident: Updater = st => st
        transferBranch(branch, v, newSt, ident, ident)
    }
  }

  // transfer function for return points
  def apply(rp: ReturnPoint): Unit = {
    val (h, v) = sem(rp)
    if (!v.isBottom) {
      for ((np @ NodePoint(call, view), x) <- sem.getRetEdges(rp)) {
        val nextNP = np.copy(node = next(call))
        val st = sem(np)
        val newSt = AbsState(st.env + (x -> v), st.heap ⊔ h)
        sem += nextNP -> newSt
      }
    }
  }

  // repl
  object REPL {
    // breakpoints
    private var continue = false
    private var breakpoints = ArrayBuffer[Regex]()

    // jline
    private val terminal: Terminal = TerminalBuilder.builder().build()
    private val completer: TreeCompleter = new TreeCompleter(
      node("log"),
      node("continue"),
      node("break"),
      node("break-list"),
      node("break-rm"),
      node("graph"),
      node("debug"),
      node("quit"),
      node("exit")
    )
    private val reader: LineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .completer(completer)
      .build()
    private val prompt: String = LINE_SEP + s"${MAGENTA}analyzer>${RESET} "
    // check break point of control point
    private def isBreak(cp: ControlPoint): Boolean = cp match {
      case NodePoint(entry: Entry, _) =>
        breakpoints.exists(_.matches(funcOf(entry).name))
      case _ => false
    }

    // run repl
    def run(cp: ControlPoint): Unit = if (!continue || isBreak(cp)) {
      println(sem.getString(cp, CYAN, true))
      try {
        while (reader.readLine(prompt) match {
          case null =>
            continue = true; false
          case line => line.split("\\s+").toList match {
            case ("quit" | "exit") :: _ =>
              breakpoints.clear()
              continue = true
              false
            case "log" :: _ =>
              stat.dump(); true
            case "continue" :: _ =>
              continue = true; false
            case "debug" :: _ => error("stop for debugging")
            case "break" :: args =>
              args.headOption match {
                case None => ???
                case Some(bp) => breakpoints += bp.r
              }; true
            case "break-rm" :: args =>
              args.headOption match {
                case None => ???
                case Some(idx) => breakpoints.remove(idx.toInt)
              }; true
            case "break-list" :: _ =>
              breakpoints.zipWithIndex.foreach {
                case (bp, i) => println(s"$i: $bp")
              }; true
            case "graph" :: args =>
              val depth = optional(args.head.toInt)
              dumpCFG(Some(cp), depth = depth); true
            case _ => continue = false; false
          }
        }) {}
      } catch {
        case e: EndOfFileException => error("stop analyze-repl")
        case e: Throwable =>
      }
    }
  }

  // dump CFG in DOT/PDF format
  def dumpCFG(
    cp: Option[ControlPoint] = None,
    pdf: Boolean = true,
    depth: Option[Int] = None
  ): Unit = try {
    val dot = (new DotPrinter)(sem, cp, depth).toString
    dumpFile(dot, s"$CFG_DIR.dot")
    if (pdf) {
      executeCmd(s"""unflatten -l 10 -o ${CFG_DIR}_trans.dot $CFG_DIR.dot""")
      executeCmd(s"""dot -Tpdf "${CFG_DIR}_trans.dot" -o "$CFG_DIR.pdf"""")
      println(s"Dumped CFG to $CFG_DIR.pdf")
    } else println(s"Dumped CFG to $CFG_DIR.dot")
  } catch {
    case _: Throwable => printlnColor(RED)(s"Cannot dump CFG")
  }

  private class Helper(ret: ReturnPoint) {
    // transfer function for normal instructions
    def transfer(inst: NormalInst): Updater = inst match {
      case IExpr(expr @ ENotSupported(msg)) =>
        import model._
        st => manualSemantics.get(msg) match {
          case Some(f) => f(expr.asite, sem, ret, st)
          case None =>
            alarm(expr.beautified)
            st
        }
      case IExpr(expr) => transfer(expr)
      case ILet(Id(x), expr) => for {
        v <- transfer(expr)
        _ <- modify(_ + (x -> v))
      } yield ()
      case IAssign(ref, expr) => for {
        refv <- transfer(ref)
        v <- transfer(expr)
        _ <- modify(_.update(sem, refv, v))
      } yield ()
      case IDelete(ref) => for {
        refv <- transfer(ref)
        _ <- modify(_.delete(sem, refv))
      } yield ()
      case IAppend(expr, list) => for {
        v <- transfer(expr)
        l <- transfer(list)
        _ <- modify(_.append(sem, v.escaped, l.escaped.addr))
      } yield ()
      case IPrepend(expr, list) => for {
        v <- transfer(expr)
        l <- transfer(list)
        _ <- modify(_.prepend(v.escaped, l.escaped.addr))
      } yield ()
      case IReturn(expr) => for {
        v <- transfer(expr)
        st <- get
        _ <- put(AbsState.Bot)
      } yield sem.doReturn(ret -> (st.heap, v.escaped.toCompletion))
      case ithrow @ IThrow(x) => for {
        addr <- id(_.allocMap(ithrow.asite, "OrdinaryObject", Map(
          "Prototype" -> AbsValue(NamedAddr(s"Global.$x.prototype")),
          "ErrorData" -> AbsUndef.Top,
        )))
        comp = AbsComp(CompThrow -> ((addr, emptyConst)))
        st <- get
      } yield sem.doReturn(ret -> ((st.heap, comp)))
      case IAssert(expr) if usePrune => for {
        pv <- pruneTransfer(expr)
        _ <- modify(pv.pruneT)
      } yield assert(pv.v, expr)
      case IAssert(expr) => for {
        v <- transfer(expr)
      } yield assert(v, expr)
      case IPrint(expr) => for {
        v <- transfer(expr)
        _ = printlnColor(GREEN)(s"[PRINT] ${beautify(v)}")
      } yield ()
      case IWithCont(id, params, bodyInst) => st => ???
      case ISetType(expr, ty) => for {
        v <- transfer(expr)
        p = v.escaped
      } yield ???
      case _ => st => ???
    }

    // transfer function for call instructions
    def transfer(call: Call, view: View): Updater = call.inst match {
      case IApp(Id(x), ERef(RefId(Id("Type"))), List(arg)) => for {
        v <- transfer(arg)
        ty <- get(_.typeOf(sem, v.escaped))
        _ <- modify(_ + (x -> ty))
      } yield ()
      case IApp(Id(x), fexpr, args) => for {
        f <- transfer(fexpr)
        vs <- join(args.map(arg => transfer(arg)))
        st <- get
        _ <- put(AbsState.Bot)
      } yield sem.doCall(call, view, st, f.escaped.clo, vs, x)
      case IAccess(Id(x), bexpr, expr, args) => for {
        b <- transfer(bexpr)
        p <- transfer(expr)
        vs <- join(args.map(arg => transfer(arg)))
        st <- get
        v = access(call, view, x, b.escaped, p.escaped.str, vs, st)
        _ <- {
          if (v.isBottom) put(AbsState.Bot)
          else modify(_ + (x -> v))
        }
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
      case expr @ EMap(Ty(ty), props) => for {
        vs <- join(props.map {
          case (kexpr, vexpr) => for {
            v <- transfer(vexpr)
            k = kexpr.to[EStr](???).str
          } yield k -> v
        })
        asite = expr.asite
        a <- id(_.allocMap(asite, ty, vs.toMap))
      } yield a
      case expr @ EList(exprs) => for {
        vs <- join(exprs.map(transfer))
        a <- id(_.allocList(expr.asite, vs.toList))
      } yield a
      case expr @ ESymbol(desc) => for {
        // TODO handling non-string descriptions
        a <- id(_.allocSymbol(expr.asite, desc.to[EStr](???).str))
      } yield a
      case EPop(list, idx) => for {
        l <- transfer(list)
        k <- transfer(idx)
        a <- id(_.pop(l.escaped, k.escaped))
      } yield a
      case ERef(ref) => for {
        refv <- transfer(ref)
        v <- get(_.lookup(sem, refv))
      } yield v
      // TODO after discussing the continuations
      case ECont(params, body) => ???
      case EUOp(uop, expr) => for {
        v <- transfer(expr)
        u = transfer(uop)(v.escaped)
      } yield u
      case EBOp(bop, left, right) => for {
        l <- transfer(left)
        r <- transfer(right)
        v = transfer(bop)(l.escaped, r.escaped)
      } yield v
      case ETypeOf(expr) => for {
        v <- transfer(expr)
        t <- get(_.typeOf(sem, v.escaped))
      } yield t
      case EIsCompletion(expr) => for {
        v <- transfer(expr)
      } yield {
        var res = AbsValue.Bot
        if (!v.comp.isBottom) res ⊔= AT
        if (!v.pure.isBottom) res ⊔= AF
        res
      }
      case EIsInstanceOf(base, name) => for {
        v <- transfer(base)
      } yield boolTop // TODO more precise
      case EGetElems(base, name) => for {
        v <- transfer(expr)
        p = v.escaped
      } yield ??? // TODO need discussion
      case EGetSyntax(base) => strTop // TODO handling non-AST values
      case EParseSyntax(code, rule, flags) => for {
        r <- transfer(rule)
        ast = r.escaped.str.gamma match {
          case Infinite => AbsAST.Top
          case Finite(set) => AbsAST.alpha(set.map(str => ASTVal(str.str)))
        }
      } yield ast
      case EConvert(source, cop, flags) => for {
        v <- transfer(source)
      } yield cop match {
        case CStrToNum => AbsNum.Top
        case CStrToBigInt => AbsBigINum.Top
        case CNumToStr => AbsStr.Top
        case CNumToInt => AbsINum.Top
        case CNumToBigInt => AbsBigINum.Top
        case CBigIntToNum => AbsNum.Top
      }
      case EContains(list, elem) => for {
        l <- transfer(list)
        e <- transfer(elem)
        c <- get(_.contains(l.escaped, e.escaped))
      } yield c
      case EReturnIfAbrupt(ERef(ref), check) => for {
        rv <- transfer(ref)
        v <- transfer(rv)
        newV <- returnIfAbrupt(v, check)
        _ <- modify(_.update(sem, rv, newV))
      } yield newV
      case EReturnIfAbrupt(expr, check) => for {
        v <- transfer(expr)
        newV <- returnIfAbrupt(v, check)
      } yield newV
      case expr @ ECopy(obj) => for {
        v <- transfer(obj)
        a <- id(_.copyOf(sem, expr.asite, v.escaped))
      } yield a
      case EKeys(obj) => for {
        v <- transfer(obj)
        a <- id(_.keysOf(v.escaped))
      } yield a
      case expr @ ENotSupported(msg) => st => {
        alarm(expr.beautified)
        (AbsValue(Absent), st)
      }
    }

    // transfer function for reference values
    def transfer(ref: Ref): Result[AbsRefValue] = ref match {
      case RefId(id) => AbsRefValue.Id(id.name)
      case RefProp(ref, expr) => for {
        rv <- transfer(ref)
        b <- transfer(rv)
        p <- transfer(expr)
        r <- AbsRefValue.Prop(b, p.escaped.str)
      } yield r // TODO handle non-string properties
    }

    // transfer function for reference values
    def transfer(refv: AbsRefValue): Result[AbsValue] =
      st => (st.lookup(sem, refv), st)

    // transfer function for unary operators
    // TODO more precise abstract semantics
    def transfer(uop: UOp): AbsPure => AbsValue = v => uop match {
      case ONeg => numTop
      case ONot => !v.escaped.bool
      case OBNot => intTop
    }

    // all booleans
    val boolTop: AbsValue = AbsBool.Top

    // transfer function for binary operators
    // TODO more precise abstract semantics
    def transfer(bop: BOp): (AbsPure, AbsPure) => AbsValue = (l, r) => bop match {
      case OPlus => arithTop
      case OSub => arithTop
      case OMul => arithTop
      case OPow => numTop
      case ODiv => numTop
      case OUMod => numTop
      case OMod => numTop
      case OLt => boolTop
      case OEq => l =^= r
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
    case class PruneValue(
      v: AbsValue,
      tlists: List[(AbsRefValue, PureValue, Boolean)] = List(),
      flists: List[(AbsRefValue, PureValue, Boolean)] = List()
    ) {
      def negate: PruneValue = PruneValue(transfer(ONot)(v.escaped), flists, tlists)
      private def prune(b: Boolean): Updater = {
        val pruneList = if (b) tlists else flists
        st => pruneList.foldLeft(st) {
          case (newSt, (refv, v, cond)) => newSt.prune(refv, v, cond)
        }
      }
      def pruneT: Updater = prune(true)
      def pruneF: Updater = prune(false)
    }
    def pruneTransfer(expr: Expr): Result[PruneValue] = expr match {
      case ERef(ref) => for {
        refv <- transfer(ref)
        v <- get(_.lookup(sem, refv))
      } yield PruneValue(
        v,
        List((refv, Bool(true), true)),
        List((refv, Bool(false), false))
      )
      case EUOp(ONot, cexpr) => for {
        pv <- pruneTransfer(cexpr)
      } yield pv.negate
      case EBOp(OEq, ERef(ref), rexpr) => for {
        refv <- transfer(ref)
        v <- transfer(refv)
        rv <- transfer(rexpr)
      } yield {
        val condv = v.escaped =^= rv.escaped
        rv.escaped.getSingle match {
          case One(pv) => PruneValue(
            condv,
            List((refv, pv, true)),
            List((refv, pv, false))
          )
          case _ => PruneValue(condv)
        }
      }
      // TODO do more pruning
      case _ => for {
        v <- transfer(expr)
      } yield PruneValue(v)
    }

    // return if abrupt completion
    def returnIfAbrupt(v: AbsValue, check: Boolean): Result[AbsValue] = st => {
      val AbsValue(pure, comp) = v
      val compV: AbsPure = comp.isNormal.map {
        case true => comp(CompNormal)._1
        case false =>
          val abrupt = comp.abrupt
          if (check) sem.doReturn(ret -> (st.heap, abrupt))
          else alarm(s"Unchecked abrupt completions: ${beautify(abrupt)}")
          AbsPure.Bot
      }.foldLeft(AbsPure.Bot)(_ ⊔ _)
      val newV: AbsValue = pure ⊔ compV
      (newV, st)
    }

    // alarm if assertion fails
    def assert(v: AbsValue, expr: Expr) =
      if (!(AT ⊑ v.escaped.bool)) alarm(s"assertion failed: ${expr.beautified}")

    // access semantics
    def access(
      call: Call,
      view: View,
      x: String,
      value: AbsPure,
      prop: AbsStr,
      args: List[AbsValue],
      st: AbsState
    ): AbsValue = {
      var v = AbsValue.Bot

      // AST cases
      for {
        ASTVal(ast) <- value.ast.gamma
        Str(name) <- prop.gamma
      } v ⊔= accessAST(call, view, x, ast, name, args, st)

      // reference cases
      v ⊔= st.lookup(sem, AbsRefValue.Prop(value, prop))

      v
    }

    // access of AST values
    def accessAST(
      call: Call,
      view: View,
      x: String,
      ast: String,
      name: String,
      args: List[AbsValue],
      st: AbsState
    ): AbsValue = (ast, name) match {
      case ("IdentifierName", "StringValue") => strTop
      case ("NumericLiteral", "NumericValue") => numTop
      case ("StringLiteral", "StringValue" | "SV") => strTop
      case _ =>
        val fids = getSyntaxFids(ast, name)
        val pairs = fids.toList.flatMap[AbsClo.Pair](fid => {
          val func = fidMap(fid)
          func.algo.head match {
            case (head: SyntaxDirectedHead) =>
              val baseArgs = sem.getArgs(head)
              sem.doCall(call, view, st, AbsClo(Clo(fid)), baseArgs ++ args, x)
              None
            case _ => None
          }
        })
        val v: AbsValue = AbsClo(AbsClo.SetD(pairs: _*))
        v
    }

    // handle this value for syntax-directed algorithms
    def handleThisValue(func: Function, st: AbsState): AbsState = {
      func.algo.head match {
        case (head: SyntaxDirectedHead) =>
          val lhsName = head.lhsName
          if (head.params.map(_.name) contains lhsName) st
          else st + (lhsName -> st.env("this")._1)
        case _ => st
      }
    }

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
  }
}
