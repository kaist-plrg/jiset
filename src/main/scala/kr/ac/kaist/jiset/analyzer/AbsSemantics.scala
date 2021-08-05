package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.js
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.{ DEBUG, LINE_SEP }
import scala.Console._
import scala.annotation.tailrec

// abstract semantics
case class AbsSemantics(
  var npMap: Map[NodePoint[Node], AbsState] = Map(),
  var rpMap: Map[ReturnPoint, AbsValue] = Map(),
  var retEdges: Map[ReturnPoint, Set[NodePoint[Call]]] = Map()
) {
  // CFG
  val cfg = js.cfg

  // REPl
  val repl = REPL(this)

  // a worklist of control points
  val worklist: Worklist[ControlPoint] = new QueueWorklist(npMap.keySet)

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
      // text-based debugging
      if (DEBUG) println(s"${cp.getFunc.name}: $cp")

      // run REPL
      if (USE_REPL) repl(Some(cp))

      // abstract transfer for the current control point
      transfer(cp)

      // keep going
      fixpoint
    }
    case None =>
      if (USE_REPL) {
        printlnColor(CYAN)(s"* Static analysis finished.")
        repl.runDirect(None)
      }
      this
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

  // get string for result of control points
  def getString(color: String): String = rpMap.keySet.toList.map(rp => {
    val ReturnPoint(func, view) = rp
    val entryCP = NodePoint(func.entry, view)
    val from = this(entryCP)
    val to = this(rp)
    setColor(color)(s"${func.name}:$view:") + s" $from ---> $to"
  }).sorted.mkString(LINE_SEP)
  def getString(cp: ControlPoint): String = getString(cp, "", true)
  def getString(cp: ControlPoint, color: String, detail: Boolean): String = {
    val func = funcOf(cp).name
    val k = setColor(color)(s"$func:$cp")
    if (detail) {
      val v = cp match {
        case (np: NodePoint[_]) => this(np).toString
        case (rp: ReturnPoint) => this(rp).toString
      }
      s"$k -> $v"
    } else k
  }
}
object AbsSemantics {
  // constructors
  def apply(script: js.ast.Script): AbsSemantics = {
    val initPair = Initialize(script)
    AbsSemantics(npMap = Map(initPair))
  }
}
