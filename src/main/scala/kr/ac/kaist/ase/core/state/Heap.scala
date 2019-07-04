package kr.ac.kaist.ase.core

import kr.ac.kaist.ase.model.Model.tyMap

// CORE Heaps
case class Heap(
    map: Map[Addr, Obj] = Map(),
    size: Int = 0
) extends CoreNode {
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

  // pushses
  def push(addr: Addr, value: Value): Heap = this(addr) match {
    case (l: CoreList) => copy(map = map + (addr -> l.push(value)))
    case v => error(s"not a list: $v")
  }

  // pops
  def pop(addr: Addr): (Value, Heap) = this(addr) match {
    case (l: CoreList) =>
      val (value, newList) = l.pop
      (value, copy(map = map + (addr -> newList)))
    case v => error(s"not a list: $v")
  }

  // copy objects
  def copyObj(addr: Addr): (Addr, Heap) = {
    val newAddr = DynamicAddr(size)
    val newMap = map + (newAddr -> apply(addr))
    val newSize = size + 1
    (newAddr, Heap(newMap, newSize))
  }

  // keys of map
  def keys(addr: Addr): (Addr, Heap) = this(addr) match {
    case (m: CoreMap) =>
      val newAddr = DynamicAddr(size)
      val newL = m.props.keys.toVector
      val newMap = map + (newAddr -> CoreList(newL))
      val newSize = size + 1
      (newAddr, Heap(newMap, newSize))
    case v => error(s"not a map: $v")
  }
  // map allocations
  def allocMap(
    ty: Ty,
    m: Map[Value, Value] = Map()
  ): (Addr, Heap) = {
    val newAddr = DynamicAddr(size)
    val newM = tyMap.getOrElse(ty.name, Map()) ++ m
    val newMap = map + (newAddr -> CoreMap(ty, newM))
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
