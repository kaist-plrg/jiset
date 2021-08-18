package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.error.AnalysisTimeout
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir
import kr.ac.kaist.jiset.js
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.{ DEBUG, LINE_SEP }
import scala.Console._
import scala.annotation.tailrec

// abstract semantics
case class AbsSemantics(
  var npMap: Map[NodePoint[Node], AbsState] = Map(),
  var rpMap: Map[ReturnPoint, AbsRet] = Map(),
  var viewMap: Map[View, Set[View]] = Map(),
  var callInfo: Map[NodePoint[Call], AbsState] = Map(),
  var retEdges: Map[ReturnPoint, Set[NodePoint[Call]]] = Map(),
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

  // assign views
  def assignView(view: View): Unit = {
    val entryView = view.entryView
    val set = viewMap.getOrElse(entryView, Set())
    viewMap += entryView -> (set + view)
  }

  // update internal map
  def +=(pair: (NodePoint[Node], AbsState)): Boolean = {
    val (np, newSt) = pair
    val oldSt = this(np)
    if (!oldSt.isBottom && USE_REPL) repl.merged = true
    if (!(newSt ⊑ oldSt)) {
      npMap += np -> (oldSt ⊔ newSt)
      assignView(np.view)
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

    val calleeView = callerView.doCall(call, astOpt)
    val params = func.params
    val np = NodePoint(func.entry, calleeView)
    this += np -> st.doCall

    val rp = ReturnPoint(func, calleeView)
    val set = retEdges.getOrElse(rp, Set())
    retEdges += rp -> (set + callerNp)

    val retT = this(rp)
    if (!retT.isBottom) worklist += rp
  }

  // update return points
  def doReturn(rp: ReturnPoint, newRet: AbsRet): Unit = {
    val ReturnPoint(func, view) = rp
    val retRp = ReturnPoint(func, view.loopExit)
    if (!newRet.value.isBottom) {
      val oldRet = this(retRp)
      if (!oldRet.isBottom && USE_REPL) repl.merged = true
      if (newRet !⊑ oldRet) {
        rpMap += retRp -> (oldRet ⊔ newRet)
        assignView(rp.view)
        worklist += retRp
      }
    }
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
  def getNps[T <: Node](np: NodePoint[T]): Set[NodePoint[T]] = for {
    view <- viewMap.getOrElse(np.view, Set())
  } yield NodePoint(np.node, view)
}
object AbsSemantics {
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
