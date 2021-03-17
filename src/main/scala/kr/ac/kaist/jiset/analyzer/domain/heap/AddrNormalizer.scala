package kr.ac.kaist.jiset.analyzer.domain.heap

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.AbsObj._

class AddrNormalizer(heap: AbsHeap) {
  // normalize
  def apply(set: Set[Addr]): Set[Addr] = {
    val namedSet = set.filter(namedAddrFilter(_)) // only contains NamedAddr
    set.filter(!namedAddrFilter(_)) ++ getLCASet(namedSet.map(toAncestors(_))).map(toAddr(_))
  }

  // helper for filtering
  def namedAddrFilter(x: Addr): Boolean = x match {
    case x: NamedAddr => true
    case _ => false
  }

  // list of NamedAddr of parents, closer ancestor in front (1st: addr itself)
  def addrParents(addr: Addr): List[Addr] = heap(addr) match {
    case MapElem(pnameOpt, _) => addr :: pnameOpt.map(pname => addrParents(NamedAddr(pname))).getOrElse(Nil)
    case _ => ??? //TODO : throw some error, must point to MapElem
  }

  // Ancestors : List of Addr, root at first
  type Ancestors = List[Addr]

  // construct Ancestors from Addr, and another
  def toAncestors(addr: Addr): Ancestors = addrParents(addr).reverse

  // restore NamedAddr from Ancestors: last element of Ancestors is LCA
  def toAddr(ans: Ancestors): Addr = ans.last

  // helper for least common ancestor
  def longestCommonPrefix(left: Ancestors, right: Ancestors): Ancestors = (left, right) match {
    case (Nil, _) | (_, Nil) => Nil
    case (l :: ls, r :: rs) => if (l == r) l :: longestCommonPrefix(ls, rs) else Nil
  }

  // find LCA Addr from list of Ancestors, which all have same root ancestor (so start with same root)
  def leastCommonAncestor(anlist: List[Ancestors]): Ancestors = anlist.reduce((x, y) => longestCommonPrefix(x, y))

  // find LCA set from list of Ancestors
  def getLCAList(anlist: List[Ancestors]): List[Ancestors] =
    anlist.groupBy(_(0)).toList.map(_._2).map(leastCommonAncestor(_))

  // wrapper for set
  def getLCASet(anset: Set[Ancestors]): Set[Ancestors] =
    getLCAList(anset.toList).toSet
}
