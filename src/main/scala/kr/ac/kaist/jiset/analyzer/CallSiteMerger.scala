package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer.domain.AbsObj._

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
