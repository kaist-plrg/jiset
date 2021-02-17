package kr.ac.kaist.jiset.analyzer

import domain._

class AbsSemantics {
  private var map: Map[View, AbsState] = Map()
  def apply(view: View): AbsState = map.getOrElse(view, AbsState.Bot)
  def +=(pair: (View, AbsState)): Unit = map += pair
}
