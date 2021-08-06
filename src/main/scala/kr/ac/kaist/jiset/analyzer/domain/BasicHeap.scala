package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// basic abstract heaps
object BasicHeap extends Domain {
  lazy val Bot = Elem(Map(), Set())

  // appender
  implicit val app: App[Elem] = (app, elem) => {
    val Elem(map, merged) = elem
    if (elem.isBottom) app >> "{}"
    else app.wrap {
      map.toList
        .sortBy(_._1.toString)
        .foreach {
          case (k, v) =>
            app :> (if (merged contains k) "[+]" else "[ ]")
            app >> " " >> s"$k -> " >> v >> LINE_SEP
        }
    }
  }

  // constructors
  def apply(
    map: Map[Loc, AbsObj] = Map(),
    merged: Set[Loc] = Set()
  ): Elem = Elem(map, merged)

  // extractors
  def unapply(elem: Elem) = Some((
    elem.map,
    elem.merged,
  ))

  // elements
  case class Elem(
    map: Map[Loc, AbsObj],
    merged: Set[Loc]
  ) extends ElemTrait {
    // partial order
    override def isBottom = map.isEmpty

    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case _ if this.isBottom => true
      case _ if that.isBottom => false
      case (Elem(lmap, lmerged), Elem(rmap, rmerged)) => {
        (lmap.keySet ++ rmap.keySet).forall(loc => {
          this(loc) ⊑ that(loc)
        }) && (lmerged subsetOf rmerged)
      }
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case _ if this.isBottom => that
      case _ if that.isBottom => this
      case (Elem(lmap, lmerged), Elem(rmap, rmerged)) => {
        val newMap = (lmap.keySet ++ rmap.keySet).toList.map(loc => {
          loc -> this(loc) ⊔ that(loc)
        }).toMap
        Elem(newMap, lmerged ++ rmerged)
      }
    }

    // singleton location checks
    def isSingle(aloc: AbsLoc): Boolean = aloc.getSingle match {
      case FlatElem(loc) => isSingle(loc)
      case _ => false
    }
    def isSingle(loc: Loc): Boolean = !(merged contains loc)

    // lookup abstract locations
    def apply(loc: Loc): AbsObj =
      map.getOrElse(loc, base.getOrElse(loc, AbsObj.Bot))
    def apply(loc: AbsLoc, prop: AbsValue): AbsValue =
      loc.map(this(_, prop)).foldLeft(AbsValue.Bot: AbsValue)(_ ⊔ _)
    def apply(loc: Loc, prop: AbsValue): AbsValue = loc match {
      case NamedLoc(js.ALGORITHM) =>
        prop.str
          .map(str => AbsValue(initHeap.getAlgorithm(str)))
          .foldLeft(AbsValue.Bot: AbsValue)(_ ⊔ _)
      case NamedLoc(js.INTRINSICS) =>
        prop.str
          .map(str => AbsValue(initHeap.getIntrinsics(str)))
          .foldLeft(AbsValue.Bot: AbsValue)(_ ⊔ _)
      case _ => this(loc)(prop)
    }

    // setters
    def update(
      aloc: AbsLoc,
      prop: AbsValue,
      value: AbsValue
    ): Elem = {
      val weak = !isSingle(aloc)
      aloc.foldLeft(this) {
        case (heap, loc) => update(loc, prop, value, weak)
      }
    }
    def update(
      loc: Loc,
      prop: AbsValue,
      value: AbsValue,
      weak: Boolean = false
    ): Elem = {
      val newObj = this(loc).update(prop, value, weak)
      copy(map = map + (loc -> newObj))
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
    def allocMap(
      ty: Ty,
      map: Map[AbsValue, AbsValue] = Map()
    )(to: Loc): Elem = {
      val obj = map.foldLeft(AbsObj(IRMap(ty))) {
        case (m, (k, v)) => m.update(k, v)
      }
      this(to) match {
        case AbsObj.Bot => Elem(this.map + (to -> obj), merged)
        case _ => Elem(this.map + (to -> (this(to) ⊔ obj)), merged + to)
      }
    }

    // list allocations
    def allocList(list: List[AbsValue])(to: Loc): Elem = ???

    // symbol allocations
    def allocSymbol(desc: AbsValue)(to: Loc): Elem = ???

    // set type of objects
    def setType(loc: Loc, ty: Ty): Elem = ???
  }

  // initial conrete heap
  lazy val initHeap: Heap = js.Initialize.initHeap

  // base mapping from locations to abstract objects
  lazy val base: Map[Loc, AbsObj] = (for {
    (addr, obj) <- initHeap.map
    loc = Loc.from(addr)
    aobj = AbsObj(obj)
  } yield loc -> aobj).toMap
}
