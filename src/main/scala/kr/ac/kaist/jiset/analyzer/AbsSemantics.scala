package kr.ac.kaist.jiset.analyzer

import domain._

class AbsSemantics {
  def apply(view: View): AbsState = ???
  def +=(pair: (View, AbsState)): Unit = ???
}
