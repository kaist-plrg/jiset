package kr.ac.kaist.jiset.analyzer.domain.value

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends value.Domain {
  def alpha(v: Value): Elem = ???
  val Bot: Elem = ???
  val Top: Elem = ???
  case class Elem() extends ElemTrait {
    def gamma: concrete.Set[Value] = ???
    def getSingle: concrete.Flat[Value] = ???
    def ⊑(that: Elem): Boolean = ???
    def ⊔(that: Elem): Elem = ???
    def ⊓(that: Elem): Elem = ???
    def boolset: Set[Boolean] = ???
  }
}
