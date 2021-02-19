package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer.domain._

class AbsSemantics {
  // internal view-wise abstract states
  private var map: Map[View, AbsState] = Map()

  // lookup
  def apply(view: View): AbsState = map.getOrElse(view, AbsState.Bot)

  // update view-wise abstract states
  def +=(pair: (View, AbsState)): Unit = map += pair

  // conversion to string
  override def toString: String = map.map {
    case (view, st) => s"$view -> $st"
  }.mkString(LINE_SEP)
}
