package kr.ac.kaist.jiset.analyzer.domain.heap

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.AbsObj._

class AddrNormalizer(heap: AbsHeap) {
  // normalize
  def apply(set: Set[Addr]): Set[Addr] = ???

  // helper
  // converts parent:Option[String] into Option[MapElem], by actually looking into this: AbsHeapElem
  def mapElemGetParent(e: MapElem): Option[MapElem] = e.parent.map(name => {
    heap(NamedAddr(name)) match {
      case o: MapElem => o
      case _ => ??? // TODO : throw some error, because if parent name exists, it should point to MapElem
    }
  })

  // AncestorRep: Represent a node and ancestors with list
  // first elem: root of the forest, second elem: the child of first elem, ... , last elem : the node
  // ex) Tree : (A (B (C D)) E) => Represent node C and its ancestors by [A, B, C]
  type AncestorRep = List[MapElem]
  def toMapElem(e: AncestorRep): MapElem = e.head

  // convert MapElem into AncestorRep
  def toAncestorRep(e: MapElem): AncestorRep = (mapElemGetParent(e) match {
    case None => Nil
    case Some(p) => p :: toAncestorRep(p) // orders closer ancester in front
  }).reverse // needs reverse to order farther ancestor in front

  // Find least common ancestor
  // LCA of two AncestorRep, Optional
  def leastCommonAncestor(left: AncestorRep, right: AncestorRep): Option[AncestorRep] = (left, right) match {
    case (Nil, _) | (_, Nil) => None
    case (l :: ls, r :: rs) => if (l == r) {
      leastCommonAncestor(ls, rs) match {
        case None => Some(List(l))
        case Some(es) => Some(l :: es)
      }
    } else None
  }

  // apply 2-wise LCA to all list,
  def leastCommonAncestor(ls: List[AncestorRep]): List[AncestorRep] = ls match {
    case Nil => Nil
    case x :: tl => leastCommonAncestor(tl) match {
      case Nil => List(x)
      case tl: List[AncestorRep] => {
        val tlLCA = leastCommonAncestor(tl)
        var xAdded = false // TODO refactor this with pure FP
        val tlLCAwithX = tlLCA.map(y => leastCommonAncestor(x, y) match {
          case None => y
          case Some(a) => xAdded = true; a
        })
        if (xAdded) tlLCAwithX else x :: tlLCAwithX
      }
    }
  }

  // wrapper as List[MapElem]
  def leastCommomAncestor(ls: List[MapElem]): List[MapElem] = leastCommonAncestor(ls.map(toAncestorRep(_))).map(toMapElem(_))
}
