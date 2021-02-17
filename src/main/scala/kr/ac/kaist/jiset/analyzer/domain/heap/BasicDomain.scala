package kr.ac.kaist.jiset.analyzer.domain.heap

import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._

object BasicDomain extends heap.Domain {
  // map domain
  val MapD = combinator.MapDomain[Addr, Obj, AbsObj.type](AbsObj)
  type MapD = MapD.Elem

  // abstraction function
  def alpha(heap: Heap): Elem = Elem(MapD(heap.map))

  // bottom value
  val Bot: Elem = Elem(MapD.Bot)

  // top value
  val Top: Elem = Elem(MapD.Top)

  // empty value
  val Empty: Elem = Elem(MapD.Empty)

  case class Elem(map: MapD = MapD.Bot) extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = this.map ⊑ that.map

    // join operator
    def ⊔(that: Elem): Elem = Elem(this.map ⊔ that.map)

    // meet operator
    def ⊓(that: Elem): Elem = Elem(this.map ⊓ that.map)

    // concretization function
    def gamma: concrete.Set[Heap] = Infinite

    // conversion to flat domain
    def getSingle: concrete.Flat[Heap] = Many

    // lookup
    def apply(addr: Addr): AbsObj = map(addr)
  }
}
