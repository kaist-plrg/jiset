package kr.ac.kaist.ase.core

// CORE Heaps
case class Heap(
    map: Map[Addr, Obj] = Map(),
    size: Int = 0
) extends CoreNode {
  // existence check
  def contains(addr: Addr): Boolean = map contains addr
  def contains(addr: Addr, prop: Value): Boolean = this(addr) contains prop

  // getters
  def apply(addr: Addr): Obj = map.getOrElse(addr, error(s"unknown address: ${beautify(addr)}"))
  def apply(addr: Addr, prop: Value): Value = this(addr)(prop)

  // setters
  def updated(addr: Addr, prop: Value, value: Value): Heap =
    copy(map = map + (addr -> this(addr).updated(prop, value)))

  // deletes
  def deleted(addr: Addr, prop: Value): Heap =
    copy(map = map + (addr -> this(addr).deleted(prop)))

  // object allocations
  def alloc(
    ty: Ty,
    objMap: Map[Value, Value] = Map()
  ): (Addr, Heap) = {
    val newAddr = DynamicAddr(size)
    val newMap = map + (newAddr -> Obj(ty, objMap))
    val newSize = size + 1
    (newAddr, Heap(newMap, newSize))
  }
}
