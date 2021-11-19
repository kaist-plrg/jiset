package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.error.AnalysisTimeout
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.ir.Bool
import kr.ac.kaist.jiset.js
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.{ DEBUG, LINE_SEP }
import scala.Console._
import scala.annotation.tailrec

// abstract semantics
case class AbsSemantics(
  var npMap: Map[NodePoint[Node], AbsState] = Map(),
  var rpMap: Map[ReturnPoint, AbsRet] = Map(),
  var callInfo: Map[NodePoint[Call], AbsState] = Map(),
  var retEdges: Map[ReturnPoint, Set[NodePoint[Call]]] = Map(),
  var loopOut: Map[View, Set[View]] = Map(),
  timeLimit: Option[Long] = None
) {
  // CFG
  val cfg = js.cfg

  // REPl
  val repl = REPL(this)

  // a worklist of control points
  val worklist: Worklist[ControlPoint] = new QueueWorklist(npMap.keySet)

  // execution
  private var checkWithInterp: Option[CheckWithInterp] = None
  def getInterp: Option[ir.Interp] = checkWithInterp.map(_.interp)

  // the number of iterations
  def getIter: Int = iter
  private var iter: Int = 0

  // get abstract return values and states of RunJobs
  val runJobs = cfg.funcMap("RunJobs")
  val runJobsRp = ReturnPoint(runJobs, View())
  def finalResult: AbsRet = this(runJobsRp)

  // abstract transfer function
  val transfer: AbsTransfer = AbsTransfer(this)

  // set start time of analyzer
  val startTime: Long = System.currentTimeMillis

  // iteration period for check
  val CHECK_PERIOD = 10000

  // log max ijk
  val irIJK = AbsSemantics.MaxIJK()
  val jsIJK = AbsSemantics.MaxIJK()

  // fixpiont computation
  @tailrec
  final def fixpoint: AbsSemantics = worklist.next match {
    case Some(cp) => {
      iter += 1

      // check time limit
      if (iter % CHECK_PERIOD == 0) timeLimit.map(limit => {
        val duration = (System.currentTimeMillis - startTime) / 1000
        if (duration > limit) throw AnalysisTimeout
      })

      // text-based debugging
      if (DEBUG) println(s"${cp.func.name}:$cp")

      // run REPL
      if (USE_REPL) repl(transfer, cp)

      // abstract transfer for the current control point
      else transfer(cp)

      // check soundness using concrete execution
      checkWithInterp.map(_.runAndCheck)

      // keep going
      fixpoint
    }
    case None =>
      // finialize REPL
      if (USE_REPL) repl.finished

      // final result
      this
  }

  // get return edges
  def getRetEdges(rp: ReturnPoint): Set[NodePoint[Call]] =
    retEdges.getOrElse(rp, Set())

  // lookup
  def apply(np: NodePoint[Node]): AbsState = npMap.getOrElse(np, AbsState.Bot)
  def apply(rp: ReturnPoint): AbsRet = rpMap.getOrElse(rp, AbsRet.Bot)

  // update internal map
  def +=(pair: (NodePoint[Node], AbsState)): Boolean = {
    val (np, newSt) = pair
    val oldSt = this(np)
    if (!oldSt.isBottom && USE_REPL) repl.merged = true
    if (!(newSt ⊑ oldSt)) {
      npMap += np -> (oldSt ⊔ newSt)
      worklist += np
      true
    }
    false
  }

  // handle calls
  def doCall(
    call: Call,
    callerView: View,
    callerSt: AbsState,
    func: Function,
    st: AbsState,
    astOpt: Option[js.ast.AST] = None
  ): Unit = {
    val callerNp = NodePoint(call, callerView)
    this.callInfo += callerNp -> callerSt

    val isJsCall = func.name match {
      case "Call" | "Construct" => true
      case _ => false
    }
    val calleeView = viewCall(callerView, call, isJsCall, astOpt)
    val np = NodePoint(func.entry, calleeView)
    this += np -> st.doCall

    // log max ijk
    if (LOG) {
      irIJK.update(calleeView.getIrIJK)
      jsIJK.update(calleeView.getJsIJK)
    }

    val rp = ReturnPoint(func, calleeView)
    val set = retEdges.getOrElse(rp, Set())
    retEdges += rp -> (set + callerNp)

    val retT = this(rp)
    if (!retT.isBottom) worklist += rp
  }

  // handle sensiticity
  def handleSens[T](l: List[T], bound: Int): List[T] =
    if (INF_SENS) l else l.take(bound)
  def handleSens(n: Int, bound: Int): Int =
    if (INF_SENS) n else n min bound

  // call transition
  def viewCall(
    callerView: View,
    call: Call,
    isJsCall: Boolean,
    astOpt: Option[AST]
  ): View = {
    val View(_, calls, _, _) = callerView
    val view = callerView.copy(
      calls = handleSens(call :: calls, IR_CALL_DEPTH),
      intraLoopDepth = 0
    )
    viewJsSens(view, isJsCall, astOpt)
  }

  // JavaScript sensitivities
  def viewJsSens(
    view: View,
    isJsCall: Boolean,
    astOpt: Option[AST]
  ): View = {
    val View(jsViewOpt, calls, loops, _) = view
    val (jsCalls, jsLoops) = (view.jsCalls, view.jsLoops)
    astOpt match {
      // flow sensitivity
      case Some(ast) =>
        val boundedLoops = loops.map {
          case LoopCtxt(loop, k) => LoopCtxt(loop, handleSens(k, LOOP_ITER))
        }
        val newJsLoops = handleSens(boundedLoops ++ jsLoops, LOOP_DEPTH)
        View(Some(JSView(ast, jsCalls, newJsLoops)), Nil, Nil, 0)

      // call-site sensitivity
      case _ if isJsCall => view.copy(jsViewOpt = jsViewOpt.map {
        case JSView(ast, calls, loops) => JSView(
          ast,
          handleSens(ast :: calls, JS_CALL_DEPTH),
          loops
        )
      })

      // non-JS part
      case _ => view
    }
  }

  // update return points
  def doReturn(rp: ReturnPoint, newRet: AbsRet): Unit = {
    val ReturnPoint(func, view) = rp
    val retRp = ReturnPoint(func, getEntryView(view))
    if (!newRet.value.isBottom) {
      val oldRet = this(retRp)
      if (!oldRet.isBottom && USE_REPL) repl.merged = true
      if (newRet !⊑ oldRet) {
        rpMap += retRp -> (oldRet ⊔ newRet)
        worklist += retRp
      }
    }
  }

  // loop transition
  def loopNext(view: View): View = view.loops match {
    case LoopCtxt(loop, k) :: rest =>
      view.copy(loops = LoopCtxt(loop, k + 1) :: rest)
    case _ => view
  }
  def loopEnter(view: View, loop: Loop): View = {
    val loopView = view.copy(
      loops = LoopCtxt(loop, 0) :: view.loops,
      intraLoopDepth = view.intraLoopDepth + 1,
    )
    loopOut += loopView -> (loopOut.getOrElse(loopView, Set()) + view)
    loopView
  }
  def loopBase(view: View): View = view.loops match {
    case LoopCtxt(loop, k) :: rest =>
      view.copy(loops = LoopCtxt(loop, 0) :: rest)
    case _ => view
  }
  def loopExit(view: View): View = {
    val views = loopOut.getOrElse(loopBase(view), Set())
    views.size match {
      case 0 => ???
      case 1 => views.head
      case _ => exploded("loop is too merged.")
    }
  }

  // get entry views of loops
  @tailrec
  final def getEntryView(view: View): View = {
    if (view.intraLoopDepth == 0) view
    else getEntryView(loopExit(view))
  }

  // get views by function name
  def getRpsByFuncName(fname: String): Set[ReturnPoint] =
    npMap.keySet.flatMap {
      case NodePoint(entry: Entry, view) =>
        val func = cfg.funcOf(entry)
        if (func.name == fname) Some(ReturnPoint(func, view))
        else None
      case _ => None
    }

  // get abstract state of control points
  def getState(cp: ControlPoint): AbsState = cp match {
    case np: NodePoint[_] => this(np)
    case rp: ReturnPoint => this(rp).state
  }

  // get string for result of control points
  def getString(
    cp: ControlPoint,
    color: String,
    detail: Boolean
  ): String = {
    val func = cp.func.name
    val cpStr = cp.toString(detail = detail)
    val k = setColor(color)(s"$func:$cpStr")
    val v = cp match {
      case (np: NodePoint[_]) => this(np).toString(detail = detail)
      case (rp: ReturnPoint) => this(rp).toString(detail = detail)
    }
    s"$k -> $v"
  }

  // check reachability based on call contexts
  def reachable(np: NodePoint[Node]): Boolean =
    !getNps(np).forall(this(_).isBottom)
  def getNps(givenNp: NodePoint[Node]): Set[NodePoint[Node]] = {
    val entryView = getEntryView(givenNp.view)
    for {
      np <- npMap.keySet
      if givenNp.node == np.node && entryView == getEntryView(np.view)
    } yield np
  }
}
object AbsSemantics {
  // maximum ijk
  case class MaxIJK(var i: Int = -1, var j: Int = -1, var k: Int = -1) {
    def update(ijk: (Int, Int, Int)): Unit = {
      val (curI, curJ, curK) = ijk
      if (curI > i) i = curI
      if (curJ > j) j = curJ
      if (curK > k) k = curK
    }
    def get: (Int, Int, Int) = (i, j, k)
    def getList: List[Int] = List(i, j, k)
    override def toString: String = s"I/J/K = $i/$j/$k"
  }

  // constructors
  def apply(
    script: js.ast.Script,
    execLevel: Int,
    timeLimit: Option[Long]
  ): AbsSemantics = {
    val initPair = Initialize(script)
    val sem = AbsSemantics(npMap = Map(initPair), timeLimit = timeLimit)
    if (execLevel >= 1) {
      sem.checkWithInterp = Some(CheckWithInterp(sem, script, execLevel))
    }
    sem
  }
}
