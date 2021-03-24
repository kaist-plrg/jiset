package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.util.Useful._

// check variables in env, list/map objects in heap has bottoms
object CheckBottoms {
  def apply(cp: ControlPoint): Unit = cp match {
    case (np: NodePoint[_]) => this(AbsSemantics(np))
    case rp: ReturnPoint => this(AbsSemantics(rp), "a return value")
  }

  def apply(st: AbsState): Unit =
    for ((x, aty) <- st.map) this(aty, s"variable $x")

  def apply(aty: AbsType, msg: String): Unit =
    if (aty.isBottom) alarm(s"Bottom result found: $msg")
}
