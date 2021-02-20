package kr.ac.kaist.jiset.analyzer.domain.env

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends env.Domain {
  // map domain
  val MapD = combinator.PMapDomain[String, Value, AbsValue.type](AbsValue)
  type MapD = MapD.Elem

  // abstraction function
  def alpha(env: Env): Elem = Elem(MapD(env.map))

  // bottom value
  val Bot: Elem = Elem(MapD.Bot)

  // top value
  val Top: Elem = Elem(MapD.Top)

  // empty value
  val Empty: Elem = Elem(MapD.Empty)

  case class Elem(map: MapD) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = this.map ⊑ that.map

    // join operator
    def ⊔(that: Elem): Elem = Elem(this.map ⊔ that.map)

    // meet operator
    def ⊓(that: Elem): Elem = Elem(this.map ⊓ that.map)

    // concretization function
    def gamma: concrete.Set[Env] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[Env] = Many
  }
}
