package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.error.NotSupported
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.{ Map => MMap }

// IR Heaps
case class Heap(
  map: MMap[Addr, Obj] = MMap(),
  var size: Int = 0
) extends IRNode {
  // getters
  def apply(addr: Addr): Obj =
    map.getOrElse(addr, error(s"unknown address: ${addr.beautified}"))
  def apply(addr: Addr, key: Value): Value = this(addr) match {
    case (s: IRSymbol) => s(key)
    case (m: IRMap) => m(key)
    case (l: IRList) => l(key)
    case IRNotSupported(_, msg) => throw NotSupported(msg)
  }

  // setters
  def update(addr: Addr, prop: Value, value: Value): this.type = this(addr) match {
    case (m: IRMap) =>
      m.update(prop, value); this
    case v => error(s"not a map: $v")
  }

  // delete
  def delete(addr: Addr, prop: Value): this.type = this(addr) match {
    case (m: IRMap) =>
      m.delete(prop); this
    case v => error(s"not a map: $v")
  }

  // appends
  def append(addr: Addr, value: Value): this.type = this(addr) match {
    case (l: IRList) =>
      l.append(value); this
    case v => error(s"not a list: $v")
  }

  // prepends
  def prepend(addr: Addr, value: Value): this.type = this(addr) match {
    case (l: IRList) =>
      l.prepend(value); this
    case v => error(s"not a list: $v")
  }

  // pops
  def pop(addr: Addr, idx: Value): Value = this(addr) match {
    case (l: IRList) => l.pop(idx)
    case v => error(s"not a list: $v")
  }

  // copy objects
  def copyObj(addr: Addr): Addr = alloc(this(addr).copy)

  // keys of map
  def keys(addr: Addr): Addr = this(addr) match {
    case (m: IRMap) =>
      val newL = m.props.toList.sortBy(_._2._2).map(_._1).toVector
      alloc(IRList(newL))
    case v => error(s"not a map: $v")
  }

  // map allocations
  def allocMap(
    ty: Ty,
    m: Map[Value, Value] = Map()
  ): Addr = {
    val irMap = IRMap(ty, ty.methods, 0L)
    for ((k, v) <- m) irMap.update(k, v)
    if (ty.hasSubMap) {
      val subMap = IRMap(Ty("SubMap"))
      irMap.update(Str("SubMap"), alloc(subMap))
    }
    alloc(irMap)
  }

  // list allocations
  def allocList(list: List[Value]): Addr = alloc(IRList(list.toVector))

  // symbol allocations
  def allocSymbol(desc: Value): Addr = alloc(IRSymbol(desc))

  // allocation helper
  private def alloc(obj: Obj): Addr = {
    val newAddr = DynamicAddr(size)
    map += newAddr -> obj
    size += 1
    newAddr
  }

  // set type of objects
  def setType(addr: Addr, ty: Ty): this.type = this(addr) match {
    case (irMap: IRMap) =>
      irMap.ty = ty; this
    case _ => error(s"invalid type update: ${addr.beautified}")
  }
}
