package kr.ac.kaist.jiset.analyzer.domain.combinator

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.ops._

// map abstract domain
class MapDomain[K, V, VD <: AbsDomain[V]](
  val AbsV: VD
) extends AbsDomain[Map[K, V]] {
  type AbsV = AbsV.Elem

  // abstraction function
  def alpha(map: Map[K, V]): Elem = {
    val m = map.map { case (k, v) => k -> AbsV(v) }
    val d = AbsV.Bot
    Elem(m, d)
  }

  // bottom value
  val Bot: Elem = Elem(Map(), AbsV.Bot)

  // top value
  val Top: Elem = Elem(Map(), AbsV.Top)

  // empty value
  val Empty: Elem = Bot

  // constructor
  def apply(map: Map[K, AbsV], default: AbsV): Elem = Elem(map, default)

  // extractor
  def unapply(elem: Elem): Option[(Map[K, AbsV], AbsV)] = Some((elem.map, elem.default))

  // pair abstract element
  case class Elem(val map: Map[K, AbsV], val default: AbsV) extends ElemTrait {
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
    def apply(k: K): AbsV = map.getOrElse(k, default)

    // strong update
    def +(pair: (K, AbsV)): Elem = update(pair._1, pair._2)
    def update(k: K, v: AbsV): Elem =
      copy(map = map + (k -> v))

    // get size
    def size: Int = map.size

    // get key set
    def keySet: Set[K] = map.keySet

    // remove keys
    def --(set: Set[K]): Elem = copy(map = map -- set)
  }
}
object MapDomain {
  def apply[K, V, VD <: EAbsDomain[V]](AbsV: VD) = new MapDomain[K, V, VD](AbsV)
}
