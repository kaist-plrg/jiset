package kr.ac.kaist.jiset.analyzer.domain.state

import kr.ac.kaist.ires.ir._

import kr.ac.kaist.jiset.analyzer.domain.concrete._

object BasicDomain extends Domain {
  def alpha(st: State): Elem = ???
  val Bot: Elem = ???
  val Top: Elem = ???
  case class Elem() extends ElemTrait {
    def gamma: FinSet[State] = ???
    def getSingle: Flat[State] = ???
    def ⊑(that: Elem): Boolean = ???
    def ⊔(that: Elem): Elem = ???
    def ⊓(that: Elem): Elem = ???
  }
}
