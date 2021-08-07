package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
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
  var retEdges: Map[ReturnPoint, Set[NodePoint[Call]]] = Map()
) {
  // CFG
  val cfg = js.cfg

  // REPl
  val repl = REPL(this)

  // a worklist of control points
  val worklist: Worklist[ControlPoint] = new QueueWorklist(npMap.keySet)

  // the number of iterations
  def getIter: Int = iter
  private var iter: Int = 0

  // get function of given control points
  def funcOf(cp: ControlPoint): Function = cp match {
    case NodePoint(node, _) => cfg.funcOf(node)
    case ReturnPoint(func, _) => func
  }

  // abstract transfer function
  val transfer: AbsTransfer = AbsTransfer(this)

  // fixpiont computation
  @tailrec
  final def fixpoint: AbsSemantics = worklist.next match {
    case Some(cp) => {
      iter += 1

      // text-based debugging
      if (DEBUG) println(s"${cp.getFunc.name}:$cp")

      // run REPL
      if (USE_REPL) repl(transfer, cp)

      // abstract transfer for the current control point
      else transfer(cp)

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
    func: Function,
    st: AbsState
  ): Unit = {
    val calleeView = callerView.doCall(call)
    val params = func.params
    val np = NodePoint(func.entry, calleeView)
    this += np -> st

    val rp = ReturnPoint(func, calleeView)
    val callerNP = NodePoint(call, callerView)
    val set = retEdges.getOrElse(rp, Set())
    retEdges += rp -> (set + callerNP)

    val retT = this(rp)
    if (!retT.isBottom) worklist += rp
  }

  // update return points
  def doReturn(rp: ReturnPoint, newRet: AbsRet): Unit = {
    if (!newRet.value.isBottom) {
      val oldRet = this(rp)
      if (!oldRet.isBottom && USE_REPL) repl.merged = true
      if (newRet !⊑ oldRet) {
        rpMap += rp -> (oldRet ⊔ newRet)
        worklist += rp
      }
    }
  }

  // get string for result of control points
  def getString(
    cp: ControlPoint,
    color: String,
    detail: Boolean
  ): String = {
    val func = funcOf(cp).name
    val cpStr = cp.toString(detail = detail)
    val k = setColor(color)(s"$func:$cpStr")
    val v = cp match {
      case (np: NodePoint[_]) => this(np).toString
      case (rp: ReturnPoint) => this(rp).toString
    }
    s"$k -> $v"
  }
}
object AbsSemantics {
  // constructors
  def apply(script: js.ast.Script): AbsSemantics = {
    val initPair = Initialize(script)
    AbsSemantics(npMap = Map(initPair))
  }
}
