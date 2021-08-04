package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js.{ Initialize => JSInitialize }

// basic abstract heaps
object BasicHeap extends Domain {
  object Bot extends Elem
  object Top extends Elem
  case class Base(map: Map[Loc, AbsObj]) extends Elem

  // constructors
  def apply(map: Map[Loc, AbsObj] = Map()) = Base(map)

  // elements
  sealed trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case BasicOrder(bool) => bool
      case (Base(lmap), Base(rmap)) => {
        (lmap.keySet ++ rmap.keySet).forall(loc => {
          this(loc) ⊑ that(loc)
        })
      }
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case BasicJoin(elem) => elem
      case (Base(lmap), Base(rmap)) => {
        val newMap = (lmap.keySet ++ rmap.keySet).toList.map(loc => {
          loc -> this(loc) ⊔ that(loc)
        }).toMap
        Base(newMap)
      }
    }

    // lookup abstract locations
    def apply(loc: Loc): AbsObj = this match {
      case Bot => AbsObj.Bot
      case Base(map) =>
        map.getOrElse(loc, base.getOrElse(loc, AbsObj.Bot))
      case Top => AbsObj.Top
    }

    // appends
    def append(loc: Loc, value: AbsValue): Elem = ???

    // prepends
    def prepend(loc: Loc, value: AbsValue): Elem = ???

    // pops
    def pop(loc: Loc, idx: AbsValue): (AbsValue, Elem) = ???

    // copy objects
    def copyObj(from: Loc)(to: Loc): Elem = ???

    // keys of map
    def keys(loc: Loc, intSorted: Boolean)(to: Loc): Elem = ???

    // map allocations
    def allocMap(ty: Ty, map: Map[AbsValue, AbsValue] = Map())(to: Loc): Elem = {
      ???
    }

    // list allocations
    def allocList(list: List[AbsValue])(to: Loc): Elem = ???

    // symbol allocations
    def allocSymbol(desc: AbsValue)(to: Loc): Elem = ???

    // set type of objects
    def setType(loc: Loc, ty: Ty): Elem = ???
  }

  // base mapping from locations to abstract objects
  lazy val base: Map[Loc, AbsObj] = (for {
    (addr, obj) <- JSInitialize.initHeap.map
    loc = Loc.from(addr)
    aobj = AbsObj(obj)
  } yield loc -> aobj).toMap
}
