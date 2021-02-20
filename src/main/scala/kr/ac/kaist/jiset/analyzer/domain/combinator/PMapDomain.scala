package kr.ac.kaist.jiset.analyzer.domain.combinator

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.ops._

// partial map abstract domain
class PMapDomain[K, V, VD <: AbsDomain[V]](
  val AbsV: VD
) extends AbsDomain[Map[K, V]] with EmptyValue {
  val AbsVOpt = OptionDomain[V, AbsV.type](AbsV)
  type AbsV = AbsV.Elem
  type AbsVOpt = AbsVOpt.Elem

  // abstraction function
  def alpha(map: Map[K, V]): Elem = {
    val m = map.map { case (k, v) => k -> AbsVOpt(Some(v)) }
    val d = AbsVOpt(None)
    Elem(m, d)
  }

  // bottom value
  val Bot: Elem = Elem(Map(), AbsVOpt.Bot)

  // top value
  val Top: Elem = Elem(Map(), AbsVOpt.Top)

  // empty value
  val Empty: Elem = Elem(Map(), AbsVOpt(None))

  // constructor
  def apply(map: Map[K, AbsVOpt], default: AbsVOpt): Elem = Elem(map, default)

  // extractor
  def unapply(elem: Elem): Option[(Map[K, AbsVOpt], AbsVOpt)] = Some((elem.map, elem.default))

  // pair abstract element
  case class Elem(val map: Map[K, AbsVOpt], val default: AbsVOpt) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = {
      val keys = this.map.keySet ++ that.map.keySet
      val mapB = keys.forall(key => this(key) ⊑ that(key))
      val defaultB = this.default ⊑ that.default
      mapB && defaultB
    }

    // join operator
    def ⊔(that: Elem): Elem = {
      val keys = this.map.keySet ++ that.map.keySet
      val map = keys.map(key => key -> (this(key) ⊔ that(key))).toMap
      val default = this.default ⊔ that.default
      Elem(map.filter(_._1 != default), default)
    }

    // meet operator
    def ⊓(that: Elem): Elem = {
      val keys = this.map.keySet ++ that.map.keySet
      val map = keys.map(key => key -> (this(key) ⊔ that(key))).toMap
      val default = this.default ⊔ that.default
      Elem(map.filter(_._1 != default), default)
    }

    // concretization function
    def gamma: concrete.Set[Map[K, V]] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[Map[K, V]] = Many

    // lookup
    def apply(k: K): AbsVOpt = map.getOrElse(k, default)
  }
}
object PMapDomain {
  def apply[K, V, VD <: EAbsDomain[V]](AbsV: VD) = new PMapDomain[K, V, VD](AbsV)
}
