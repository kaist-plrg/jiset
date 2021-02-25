package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._

// heaps
case class Heap(
  map: Map[Addr, Obj],
  size: Long = 0L
) {
  def get(addr: Addr): Option[Obj] = map.get(addr)
  def apply(addr: Addr): Obj = map.getOrElse(addr, error(s"Unknown address"))

  // alloc
  def allocList(list: List[Value]): (Addr, Heap) = {
    val addr = DynamicAddr(size)
    val newList = ListObj(list)
    val newMap = map + (addr -> newList)
    (addr, Heap(newMap, size + 1))
  }
  def allocMap(ty: Ty, svMap: Map[String, Value]): (Addr, Heap) = {
    val addr = DynamicAddr(size)
    val newSize = size + 1
    val newObj = MapObj(svMap) // TODO where should Ty fit in?
    (addr, Heap(map + (addr -> newObj), newSize))
  }
  def allocSymbol(desc: String): (Addr, Heap) = {
    val addr = DynamicAddr(size)
    val newSize = size + 1
    val newObj = SymbolObj(desc)
    (addr, Heap(map + (addr -> newObj), newSize))
  }

  // interp helpers
  def copyObj(addr: Addr): (Addr, Heap) = {
    val addr = DynamicAddr(size)
    val newSize = size + 1
    (addr, Heap(map + (addr -> apply(addr)), newSize))
  }
  def mapObjKeys(addr: Addr): (Addr, Heap) = this(addr) match {
    case m: MapObj =>
      val addr = DynamicAddr(size)
      val newSize = size + 1
      val keyList = m.props.keys.toList
      val keyListObj = ListObj(keyList.map(s => Str(s)))
      (addr, Heap(map + (addr -> keyListObj)))
    case _ => error("Key can only be applied to MapObj")
  }
}
object Heap { def apply(seq: (Addr, Obj)*): Heap = Heap(seq.toMap) }
