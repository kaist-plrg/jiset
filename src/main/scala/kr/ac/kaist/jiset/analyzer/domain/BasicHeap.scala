package kr.ac.kaist.jiset.analyzer.domain

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer._
import kr.ac.kaist.jiset.ir.{ AllocSite => _, _ }
import kr.ac.kaist.jiset.js
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._

// basic abstract heaps
object BasicHeap extends Domain {
  lazy val Bot = Elem(Map(), Set(), LocStat(), LocStat(), Set())

  // initial conrete heap
  lazy val initHeap: Heap = js.Initialize.initHeap

  // base mapping from locations to abstract objects
  lazy val base: Map[Loc, AbsObj] = (for {
    (addr, obj) <- initHeap.map
    loc = Loc.from(addr)
    aobj = AbsObj(obj)
  } yield loc -> aobj).toMap

  // location status
  case class LocStat(
    allocs: Set[Loc] = Set(),
    touched: Set[Loc] = Set()
  ) {
    // conversion to string
    def getString(loc: Loc): String = {
      if (allocs contains loc) "A"
      else if (touched contains loc) "T"
      else " "
    }

    // partial order
    def ⊑(that: LocStat): Boolean = (
      (this.allocs subsetOf that.allocs) &&
      (this.touched subsetOf that.touched)
    )

    // join operator
    def ⊔(that: LocStat): LocStat = LocStat(
      this.allocs ++ that.allocs,
      this.touched ++ that.touched,
    )

    // remove given locations
    def removeLocs(locs: Set[Loc]): LocStat =
      LocStat(allocs -- locs, touched -- locs)

    // allocation helper
    def alloc(loc: Loc): LocStat = LocStat(allocs + loc, touched + loc)

    // touch helper
    def touch(loc: Loc): LocStat = LocStat(allocs, touched + loc)
  }

  // appender
  implicit val app: App[Elem] = (app, elem) => {
    val Elem(map, fixed, pstat, fstat, merged) = elem
    if (elem.isBottom) app >> "{}"
    else app.wrap {
      map.toList
        .sortBy(_._1.toString)
        .foreach {
          case (k, v) =>
            app :> "["
            if (USE_GC) {
              app >> (if (fixed contains k) "F" else " ")
              app >> pstat.getString(k)
              app >> fstat.getString(k)
            }
            app >> (if (merged contains k) "M" else " ")
            app >> "] " >> s"$k -> " >> v >> LINE_SEP
        }
    }
  }

  // constructors
  def apply(
    map: Map[Loc, AbsObj] = Map(),
    fixed: Set[Loc] = Set(),
    pstat: LocStat = LocStat(),
    fstat: LocStat = LocStat(),
    merged: Set[Loc] = Set()
  ): Elem = Elem(map, fixed, pstat, fstat, merged)

  // extractors
  def unapply(elem: Elem) = Some((
    elem.map,
    elem.merged,
  ))

  // elements
  case class Elem(
    map: Map[Loc, AbsObj],
    fixed: Set[Loc],
    pstat: LocStat,
    fstat: LocStat,
    merged: Set[Loc]
  ) extends ElemTrait {
    // partial order
    override def isBottom = map.isEmpty

    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case _ if this.isBottom => true
      case _ if that.isBottom => false
      case (l, r) => (
        (l.map.keySet ++ r.map.keySet).forall(loc => {
          this(loc) ⊑ that(loc)
        }) &&
        (l.fixed subsetOf r.fixed) &&
        (l.pstat ⊑ r.pstat) &&
        (l.fstat ⊑ r.fstat) &&
        (l.merged subsetOf r.merged)
      )
    }

    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case _ if this.isBottom => that
      case _ if that.isBottom => this
      case (l, r) => Elem(
        map = (l.map.keySet ++ r.map.keySet).toList.map(loc => {
          loc -> this(loc) ⊔ that(loc)
        }).toMap,
        fixed = l.fixed ++ r.fixed,
        pstat = l.pstat ⊔ r.pstat,
        fstat = l.fstat ⊔ r.fstat,
        merged = l.merged ++ r.merged,
      )
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

    // handle calls
    def doCall: Elem = if (USE_GC) {
      copy(fstat = LocStat())
    } else this
    def doProcStart(fixed: Set[Loc]): Elem = if (USE_GC) {
      copy(fixed = fixed, pstat = LocStat(), fstat = LocStat())
    } else this

    // handle returns (this: caller heaps / retHeap: return heaps)
    def doReturn(to: Elem): Elem = if (USE_GC) Elem(
      map = (fixed ++ fstat.touched).foldLeft(to.map) {
        case (map, loc) => map + (loc -> this(loc))
      },
      fixed = fixed,
      pstat = pstat,
      fstat = this.fstat ⊔ to.fstat,
      merged = (
        this.merged ++
        to.merged ++
        (to.map.keySet intersect this.fstat.allocs)
      ),
    )
    else this
    def doProcEnd(to: Elem): Elem = if (USE_GC) Elem(
      map = pstat.touched.foldLeft(to.map) {
        case (map, loc) => map + (loc -> this(loc))
      },
      fixed = to.fixed,
      pstat = this.pstat ⊔ to.pstat,
      fstat = this.pstat ⊔ to.fstat,
      merged = (
        this.merged ++
        to.merged ++
        (to.map.keySet intersect this.pstat.allocs)
      ),
    )
    else this

    // get reachable locations
    def reachableLocs(initLocs: Set[Loc]): Set[Loc] = {
      var visited = Set[Loc]()
      var reached = Set[Loc]()
      def aux(loc: Loc): Unit = if (!visited.contains(loc)) {
        visited += loc
        if (!loc.isNamed) reached += loc
        this(loc).reachableLocs.filter(!_.isNamed).foreach(aux)
      }
      map.keys.filter(_.isNamed).foreach(aux)
      initLocs.filter(!_.isNamed).foreach(aux)
      reached
    }

    // remove given locations
    def removeLocs(locs: Loc*): Elem = removeLocs(locs.toSet)
    def removeLocs(locs: Set[Loc]): Elem = {
      val realLocs = locs -- fixed
      Elem(
        map -- realLocs,
        fixed,
        pstat,
        fstat.removeLocs(realLocs),
        merged -- realLocs,
      )
    }

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
        val subMapObj = AbsObj.OrderedMap(Ty("SubMap"), Map(), Vector())
        this
          .alloc(to, newObj.update(AbsValue("SubMap"), AbsValue(subMapLoc), weak = false))
          .alloc(subMapLoc, subMapObj)
      } else this.alloc(to, newObj)
    }

    // list allocations
    def allocList(
      values: Iterable[AbsValue] = Nil
    )(to: AllocSite): Elem = alloc(to, AbsObj.KeyWiseList(values.toVector))

    // symbol allocations
    def allocSymbol(
      desc: AbsValue
    )(to: AllocSite): Elem = alloc(to, AbsObj.SymbolElem(desc))

    // allocation helper
    private def alloc(loc: Loc, obj: AbsObj): Elem = this(loc) match {
      case AbsObj.Bot => Elem(
        map = map + (loc -> obj),
        fixed,
        pstat.alloc(loc),
        fstat.alloc(loc),
        merged = merged,
      )
      case _ => Elem(
        map = map + (loc -> (this(loc) ⊔ obj)),
        fixed,
        pstat.alloc(loc),
        fstat.alloc(loc),
        merged = merged + loc
      )
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
          Elem(
            map = heap.map + (loc -> newObj),
            fixed,
            pstat.touch(loc),
            fstat.touch(loc),
            merged
          )
      }
    }
    private def applyFold(loc: AbsLoc)(f: AbsObj => AbsObj): AbsObj = {
      loc.toList.foldLeft(AbsObj.Bot: AbsObj) {
        case (obj, loc) => obj ⊔ f(this(loc))
      }
    }
  }
}
