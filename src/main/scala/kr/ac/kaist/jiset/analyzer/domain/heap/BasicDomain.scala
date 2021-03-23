package kr.ac.kaist.jiset.analyzer.domain.heap

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.Beautifier._
import kr.ac.kaist.jiset.util.Useful._

object BasicDomain extends heap.Domain {
  // map domain
  val MapD = combinator.MapDomain[Loc, Obj, AbsObj.type](AbsObj)
  type MapD = MapD.Elem

  // abstraction function
  def alpha(heap: Heap): Elem = Elem(MapD(heap.map.map {
    case (addr, obj) => addr.toLoc -> obj
  }))

  // bottom value
  val Bot: Elem = Elem(MapD.Bot)

  // top value
  val Top: Elem = Elem(MapD.Top)

  // empty value
  val Empty: Elem = Bot

  // constructor
  def apply(map: MapD = MapD.Bot): Elem = Elem(map)

  // extractor
  def unapply(elem: Elem): Option[MapD] = Some(elem.map)

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
    def apply(loc: Loc): AbsObj = map(loc)

    // update
    def +(pair: (Loc, AbsObj)): Elem = copy(map = map + pair)
    def update(loc: Loc, obj: AbsObj): Elem = copy(map = map.update(loc, obj))
    def <<(that: Elem): Elem = copy(map = this.map << that.map)

    // get size
    def size: Int = map.size

    // get locations
    def keySet: Set[Loc] = map.keySet

    // remove locations
    def --(set: Set[Loc]): Elem = copy(map = map -- set)

    // lookup locations
    def lookupLoc(sem: AbsSemantics, loc: Loc): AbsObj = loc match {
      case (_: NamedAddr) => sem.globalHeap.getOrElse(loc, {
        alarm(s"unknown locations: ${beautify(loc)}")
        AbsObj.Bot
      })
      case _ => this(loc)
    }
  }
}
