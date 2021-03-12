package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.util.Useful._

// check variables in env, list/map objects in heap has bottoms
class CheckBottoms(sem: AbsSemantics) {
  def apply(cp: ControlPoint): Unit = cp match {
    case (np: NodePoint[_]) => this(sem(np))
    case rp: ReturnPoint =>
      val (resH, resV) = sem(rp)
      this(resH)
      this(resV, "a return value")
  }
  def apply(v: AbsValue, msg: String): Unit =
    if (v.isBottom) alarm(s"Bottom result found: $msg")
  def apply(st: AbsState): Unit = {
    this(st.env); this(st.heap)
  }
  def apply(heap: AbsHeap): Unit = {
    import AbsObj._
    for ((addr, obj) <- heap.map.map) obj match {
      case MapElem(_, map) => for ((x, vOpt) <- map.map) {
        if (vOpt.absent.isBottom)
          this(vOpt.value, s"a map object @ ${beautify(addr)}")
      }
      case _ =>
    }
  }
  def apply(env: AbsEnv): Unit = for ((x, vOpt) <- env.map.map) {
    if (vOpt.absent.isBottom) this(vOpt.value, s"variable $x")
  }
}
