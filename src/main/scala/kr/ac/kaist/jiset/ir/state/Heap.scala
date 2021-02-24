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
  def allocMap(ty: Ty, svMap: Map[String, Value]): (Addr, Heap) = {
    val addr = DynamicAddr(size)
    val newSize = size + 1
    val newMap = MapObj(svMap) // TODO where should Ty fit in?
    (addr, Heap(map + (addr -> newMap), newSize))
  }
}
object Heap { def apply(seq: (Addr, Obj)*): Heap = Heap(seq.toMap) }
