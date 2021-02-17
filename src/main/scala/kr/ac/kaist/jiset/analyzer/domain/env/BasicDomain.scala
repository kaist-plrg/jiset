package kr.ac.kaist.jiset.analyzer.domain.env

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.analyzer.state._
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends env.Domain {
  // map domain
  val MapD = combinator.MapDomain[Id, Value, AbsValue.type](AbsValue)
  type MapD = MapD.Elem

  // abstraction function
  def alpha(env: Env): Elem = Elem(MapD(env.locals))

  // bottom value
  val Bot: Elem = Elem(MapD.Bot)

  // top value
  val Top: Elem = Elem(MapD.Top)

  case class Elem(locals: MapD) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = this.locals ⊑ that.locals

    // join operator
    def ⊔(that: Elem): Elem = Elem(this.locals ⊔ that.locals)

    // meet operator
    def ⊓(that: Elem): Elem = Elem(this.locals ⊓ that.locals)

    // concretization function
    def gamma: concrete.Set[Env] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[Env] = Many
  }
}
