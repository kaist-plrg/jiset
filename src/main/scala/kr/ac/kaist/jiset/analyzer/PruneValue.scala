package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.util._

case class PruneValue(
  sem: AbsSemantics,
  v: AbsValue,
  tlists: List[PruneCase] = List(),
  flists: List[PruneCase] = List()
) {
  def negate: PruneValue = PruneValue(sem, !v.escaped.bool, flists, tlists)
  private def prune(b: Boolean): AbsState => AbsState = {
    val pruneList = if (b) tlists else flists
    st => pruneList.foldLeft(st) {
      case (st, PruneCase(refv, target, cond)) =>
        st.prune(sem, refv, target, cond)
    }
  }
  def pruneT: AbsState => AbsState = prune(true)
  def pruneF: AbsState => AbsState = prune(false)
}

case class PruneCase(
  refv: AbsRefValue,
  target: PruneTarget,
  cond: Boolean
)

sealed trait PruneTarget
case class PruneSingle(pure: PureValue) extends PruneTarget
case class PruneType(name: String) extends PruneTarget
