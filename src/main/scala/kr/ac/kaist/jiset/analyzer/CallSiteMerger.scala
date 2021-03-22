package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer.domain.AbsObj._

class CallSiteMerger(mergeMap: Map[Loc, CallSite]) {
  def apply(heap: AbsHeap): AbsHeap = {
    var resHeap: AbsHeap = AbsHeap.Bot
    for ((loc, absVal) <- heap.map.map) {
      val newLoc = visit(loc)
      val newVal = visit(absVal)
      if (resHeap.map.map contains newLoc) {
        val oldVal = resHeap(newLoc)
        resHeap += (newLoc, oldVal ⊔ newVal)
      } else {
        resHeap += (newLoc, newVal)
      }
    }
    resHeap
  }

  def apply(env: AbsEnv): AbsEnv = {
    val origMap: AbsEnv.MapD = env.map
    var newMap: AbsEnv.MapD = AbsEnv.MapD.Empty
    origMap.map.foreach({
      case (s, vopt) =>
        val newVal: AbsValue = visit(vopt.value)
        newMap += (s, newVal)
    })
    AbsEnv(newMap)
  }

  def apply(value: AbsValue): AbsValue = visit(value)

  // Helper functions, recursively visits sub-AbsValue and change Loc into CallSite according to mergeMap
  private def visit(value: AbsValue): AbsValue = value.copy(pure = visit(value.pure), comp = visit(value.comp))

  private def visit(obj: AbsObj): AbsObj = obj match {
    case AbsObj.Top => AbsObj.Top
    case mobj: AbsObj.MapElem =>
      MapElem(
        mobj.parent,
        MapD(mobj.map.map.map({ case (s, v) => (s, visit(v)) }), visit(mobj.map.default))
      )
    case lobj: AbsObj.ListElem =>
      ListElem(ListD(visit(lobj.list.value)))
    case _ => obj
  }

  private def visit(vopt: MapD.AbsVOpt): MapD.AbsVOpt.Elem = vopt.copy(value = visit(vopt.value))

  private def visit(pure: AbsPure): AbsPure = pure.copy(
    loc = visit(pure.loc) //TODO is this enough?
  )

  private def visit(comp: AbsComp): AbsComp = comp.copy(
    map = comp.map.map({
      case ((ty, (value, target))) => (ty, (visit(value), visit(target)))
    })
  )

  private def visit(loc: AbsLoc): AbsLoc = loc.map(a => mergeMap.getOrElse(a, a)) // ASSUME: set auto-merges identical Loc

  private def visit(loc: Loc): Loc = mergeMap.getOrElse(loc, loc)
}

object CallSiteMerger {
  def getLocType(heap: AbsHeap, loc: Loc): Option[LocType] = heap(loc) match {
    case MapElem(Some(p), map) => Some(LocType(p))
    case _ => None
  }

  def mergeMap(heap: AbsHeap, fid: Int, k: Int, given: Set[Loc]): Map[Loc, CallSite] = {
    val targetAddrs: Set[Loc] = heap.map.map.keySet diff given
    val grouped: Map[LocType, Set[Loc]] = targetAddrs.map(loc => (loc, getLocType(heap, loc)))
      .filter(_._2 != None)
      .map(_ match {
        case (l, Some(lty)) => (l, lty)
        case _ => ???
      })
      .groupBy(_._2) //: Map[LocType, Set[(Loc, LocType)]]
      .map({ case (lt, set) => (lt, set.map(_._1)) })

    var resultMap: Map[Loc, CallSite] = Map()
    grouped.foreach({
      case (lty, lset) =>
        lset.foreach(loc => resultMap += (loc -> CallSite(fid, k, lty)))
    })
    resultMap
  }
}
