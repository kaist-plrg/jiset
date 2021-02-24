package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._

// heaps
case class Heap(
  map: Map[Addr, Obj],
  size: Long = 0L
) {
  def get(addr: Addr): Option[Obj] = map.get(addr)
  def apply(addr: Addr): Obj = map.getOrElse(addr, error(s"Unknown address"))

  def allocList(list: List[Value]): (Addr, Heap) = {
    val addr = DynamicAddr(size)
    val newList = ListObj(list)
    val newMap = map + (addr -> newList)
    (addr, Heap(newMap, size + 1))
  }
}
object Heap { def apply(seq: (Addr, Obj)*): Heap = Heap(seq.toMap) }
