package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir.{ AllocSite => _, _ }
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

    // singleton checks
    def isSingle: Boolean = map.forall {
      case (loc, obj) => isSingle(loc) && obj.isSingle
    }

    // singleton location checks
    def isSingle(aloc: AbsLoc): Boolean = aloc.getSingle match {
      case FlatElem(loc) => isSingle(loc)
      case _ => false
    }
    def isSingle(loc: Loc): Boolean = !(merged contains loc)

    // get reachable locations
    def reachableLocs(initLocs: Set[Loc]): Set[Loc] = {
      var locs = Set[Loc]()
      def aux(loc: Loc): Unit = if (!locs.contains(loc)) {
        locs += loc
        this(loc).reachableLocs.foreach(aux)
      }
      initLocs.foreach(aux)
      locs
    }

    // remove given locations
    def removeLocs(locs: Loc*): Elem = removeLocs(locs.toSet)
    def removeLocs(locs: Set[Loc]): Elem = Elem(map -- locs, merged -- locs)

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
    def update(loc: AbsLoc, prop: AbsValue, value: AbsValue): Elem =
      applyEach(loc)(_.update(prop, value, _))

    // delete
    def delete(loc: AbsLoc, prop: AbsValue): Elem =
      applyEach(loc)(_.delete(prop, _))

    // appends
    def append(loc: AbsLoc, value: AbsValue): Elem =
      applyEach(loc)(_.append(value, _))

    // prepends
    def prepend(loc: AbsLoc, value: AbsValue): Elem =
      applyEach(loc)(_.prepend(value, _))

    // pops
    def pop(loc: AbsLoc, idx: AbsValue): (AbsValue, Elem) = {
      var v: AbsValue = AbsValue.Bot
      val h: Elem = applyEach(loc)((obj, weak) => {
        val (newV, newObj) = obj.pop(idx, weak)
        v ⊔= newV
        newObj
      })
      (v, h)
    }

    // copy objects
    def copyObj(
      from: AbsLoc
    )(to: AllocSite): Elem = alloc(to, applyFold(from)(obj => obj))

    // keys of map
    def keys(
      loc: AbsLoc,
      intSorted: Boolean
    )(to: AllocSite): Elem = alloc(to, applyFold(loc)(_.keys(intSorted)))

    // map allocations
    def allocMap(
      ty: Ty,
      pairs: List[(AbsValue, AbsValue)]
    )(to: AllocSite): Elem = {
      val newObj = (pairs.foldLeft(AbsObj(IRMap(ty))) {
        case (m, (k, v)) => m.update(k, v, weak = false)
      })
      if (ty.hasSubMap) {
        val subMapLoc = SubMapLoc(to)
        val subMapObj = AbsObj.MapElem(Ty("SubMap"), Map(), Vector())
        this
          .alloc(to, newObj.update(AbsValue("SubMap"), AbsValue(subMapLoc), weak = false))
          .alloc(subMapLoc, subMapObj)
      } else this.alloc(to, newObj)
    }

    // list allocations
    def allocList(
      values: Iterable[AbsValue] = Nil
    )(to: AllocSite): Elem = alloc(to, AbsObj.ListElem(values.toVector))

    // symbol allocations
    def allocSymbol(
      desc: AbsValue
    )(to: AllocSite): Elem = alloc(to, AbsObj.SymbolElem(desc))

    // allocation helper
    private def alloc(loc: Loc, obj: AbsObj): Elem = this(loc) match {
      case AbsObj.Bot => Elem(this.map + (loc -> obj), merged)
      case _ => Elem(this.map + (loc -> (this(loc) ⊔ obj)), merged + loc)
    }

    // set type of objects
    def setType(loc: AbsLoc, ty: Ty): Elem =
      applyEach(loc)((obj, _) => obj.setType(ty))

    // check contains
    def contains(loc: AbsLoc, value: AbsValue): AbsBool = {
      loc.toList.foldLeft(AbsBool.Bot: AbsBool) {
        case (bool, loc) => bool ⊔ (this(loc) contains value)
      }
    }

    // helper for abstract locations
    private def applyEach(loc: AbsLoc)(
      f: (AbsObj, Boolean) => AbsObj
    ): Elem = {
      val weak = !isSingle(loc)
      loc.toList.foldLeft(this) {
        case (heap, loc) =>
          val obj = heap(loc)
          val newObj = f(obj, weak)
          heap.copy(map = heap.map + (loc -> newObj))
      }
    }
    private def applyFold(loc: AbsLoc)(f: AbsObj => AbsObj): AbsObj = {
      loc.toList.foldLeft(AbsObj.Bot: AbsObj) {
        case (obj, loc) => obj ⊔ f(this(loc))
      }
    }
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
