package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.DEBUG
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util._
import scala.annotation.tailrec

// abstract semantics
case class AbsSemantics(
  var npMap: Map[NodePoint[Node], AbsState] = Map(),
  var rpMap: Map[ReturnPoint, AbsValue] = Map(),
  var retEdges: Map[ReturnPoint, Set[NodePoint[Call]]] = Map()
) {
  // a worklist of control points
  val worklist: Worklist[ControlPoint] = new QueueWorklist(npMap.keySet)

  // abstract transfer function
  val transfer: AbsTransfer = AbsTransfer(this)

  // fixpiont computation
  @tailrec
  final def fixpoint: AbsSemantics = worklist.next match {
    case Some(cp) => {
      // text-based debugging
      if (DEBUG) {
        println(s"${cp.getFunc.name}: $cp")
      }

      // abstract transfer for the current control point
      transfer(cp)

      // keep going
      fixpoint
    }
    case None => this
  }

  // get return edges
  def getRetEdges(rp: ReturnPoint): Set[NodePoint[Call]] =
    retEdges.getOrElse(rp, Set())

  // lookup
  def apply(np: NodePoint[Node]): AbsState = npMap.getOrElse(np, AbsState.Bot)
  def apply(rp: ReturnPoint): AbsValue = rpMap.getOrElse(rp, AbsValue.Bot)

  // update internal map
  def +=(pair: (NodePoint[Node], AbsState)): Boolean = {
    val (np, newSt) = pair
    val oldSt = this(np)
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
    val calleeView = callerView // TODO
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
}
object AbsSemantics {
  // constructors
  def apply(script: Script): AbsSemantics = {
    val initPair = Initialize(script)
    AbsSemantics(npMap = Map(initPair))
  }
}
