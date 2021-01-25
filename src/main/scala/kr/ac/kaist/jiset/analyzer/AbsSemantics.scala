package kr.ac.kaist.jiset.analyzer

import domain._

class AbsSemantics {
  private var map: Map[View, AbsState] = Map()
  def apply(view: View): AbsState = map(view)
  def +=(pair: (View, AbsState)): Unit = map += pair
}
