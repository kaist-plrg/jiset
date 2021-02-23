package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.cfg._

class AbsSemantics(val cfg: CFG) {
  import cfg._

  // worklist
  val worklist = new StackWorklist[ControlPoint]

  // internal map from control points to abstract states
  private var npMap: Map[NodePoint, AbsState] = Map()
  private var rpMap: Map[ReturnPoint, (AbsHeap, AbsValue)] = Map()

  // lookup
  def apply(np: NodePoint): AbsState = npMap.getOrElse(np, AbsState.Bot)
  def apply(rp: ReturnPoint): (AbsHeap, AbsValue) =
    rpMap.getOrElse(rp, (AbsHeap.Bot, AbsValue.Bot))

  // update internal map
  def +=(pair: (NodePoint, AbsState)): Unit = {
    val (np, newSt) = pair
    val oldSt = this(np)
    if (!(newSt ⊑ oldSt)) {
      npMap += np -> (oldSt ⊔ newSt)
      worklist += np
    }
  }
  def doReturn(pair: (ReturnPoint, (AbsHeap, AbsValue))): Unit = {
    val (rp, (newH, newV)) = pair
    val (oldH, oldV) = this(rp)
    if (!(newH ⊑ oldH && newV ⊑ oldV)) {
      rpMap += rp -> (oldH ⊔ newH, oldV ⊔ newV)
      worklist += rp
    }
  }

  // get string for result of control points
  def getString(cp: ControlPoint): String = cp match {
    case np @ NodePoint(entry: Entry, view) =>
      val st = this(np)
      s"${funcOf(entry).name}:$view -> ${beautify(st)}"
    case (np: NodePoint) =>
      val st = this(np)
      s"$np -> ${beautify(st)}"
    case (rp: ReturnPoint) =>
      val (h, v) = this(rp)
      s"$rp -> [RETURN] ${beautify(v)}" + (
        if (h.isBottom) ""
        else s" @ ${beautify(h)}"
      )
  }
}
