package kr.ac.kaist.ase.core

// CORE Heaps
case class Heap(
    map: Map[Addr, Obj] = Map(),
    size: Int = 0
) extends CoreNode {
  // existence check
  def contains(addr: Addr): Boolean = map contains addr
  def contains(addr: Addr, prop: Value): Boolean = this(addr) match {
    case (m: CoreMap) => m contains prop
    case v => error(s"not a map: $v")
  }

  // getters
  def apply(addr: Addr): Obj = map.getOrElse(addr, error(s"unknown address: ${beautify(addr)}"))
  def apply(addr: Addr, key: Value): Value = this(addr) match {
    case (m: CoreMap) => m(key)
    case (l: CoreList) => l(key)
    case v => error(s"not a map or a list: $v")
  }

  // setters
  def updated(addr: Addr, prop: Value, value: Value): Heap = this(addr) match {
    case (m: CoreMap) => copy(map = map + (addr -> m.updated(prop, value)))
    case v => error(s"not a map: $v")
  }

  // deletes
  def deleted(addr: Addr, prop: Value): Heap = this(addr) match {
    case (m: CoreMap) => copy(map = map + (addr -> m.deleted(prop)))
    case v => error(s"not a map: $v")
  }

  // map allocations
  def allocMap(
    ty: Ty,
    m: Map[Value, Value] = Map()
  ): (Addr, Heap) = {
    val newAddr = DynamicAddr(size)
    val newMap = map + (newAddr -> CoreMap(ty, m))
    val newSize = size + 1
    (newAddr, Heap(newMap, newSize))
  }

  // list allocations
  def allocList(list: List[Value]): (Addr, Heap) = {
    val newAddr = DynamicAddr(size)
    val newList = map + (newAddr -> CoreList(list.toVector))
    val newSize = size + 1
    (newAddr, Heap(newList, newSize))
  }
}
