package kr.ac.kaist.jiset.analyzer.domain.state

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends state.Domain {
  def alpha(st: State): Elem = ???
  val Bot: Elem = ???
  val Top: Elem = ???
  case class Elem() extends ElemTrait {
    def gamma: concrete.Set[State] = ???
    def getSingle: concrete.Flat[State] = ???
    def ⊑(that: Elem): Boolean = ???
    def ⊔(that: Elem): Elem = ???
    def ⊓(that: Elem): Elem = ???
  }
}
