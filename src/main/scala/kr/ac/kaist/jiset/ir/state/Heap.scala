package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._

// heaps
case class Heap(
  map: Map[Addr, Obj],
  size: Int = 0
) {
  // getter
  def get(addr: Addr): Option[Obj] = map.get(addr)
  def apply(addr: Addr): Obj = map.getOrElse(addr, error(s"Unknown address"))
  def apply(addr: Addr, prop: String): Value = this(addr) match {
    case MapObj(_, props) => props.getOrElse(prop, Absent)
    case ListObj(values) => prop match {
      case "length" => INum(values.length)
      case _ =>
        val idx = prop.toInt
        if (0 <= idx && idx < values.length) values(idx)
        else Absent
    }
    case SymbolObj(desc) => prop match {
      case "Description" => Str(desc)
      case _ => error(s"an invalid symbol field access: $prop")
    }
  }

  // updated
  def updated(addr: Addr, key: String, v: Value): Heap = {
    val obj = this(addr) match {
      case MapObj(ty, props) => MapObj(ty, props + (key -> v))
      case ListObj(values) =>
        val idx = key.toInt
        if (idx < 0 || idx >= values.length) error(s"Out of range: $idx of $this")
        ListObj(values.updated(idx, v))
      case _ => error(s"not a map or list: $v")
    }
    copy(map = map + (addr -> obj))
  }

  // deleted
  def deleted(addr: Addr, prop: String): Heap = this(addr) match {
    case MapObj(ty, props) => copy(map = map + (addr -> MapObj(ty, props - prop)))
    case obj @ _ => error(s"not a map: $obj")
  }

  // append
  def append(addr: Addr, v: Value): Heap = this(addr) match {
    case ListObj(values) => copy(map = map + (addr -> ListObj(values :+ v)))
    case obj @ _ => error(s"not a list: $obj")
  }

  // prepend
  def prepend(addr: Addr, v: Value): Heap = this(addr) match {
    case ListObj(values) => copy(map = map + (addr -> ListObj(v :: values)))
    case obj @ _ => error(s"not a list: $obj")
  }

  // set type
  def setType(addr: Addr, ty: Ty): Heap = this(addr) match {
    case MapObj(ty, props) => copy(map = map + (addr -> MapObj(ty, props)))
    case obj @ _ => error(s"not a list: $obj")
  }

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
    val newObj = MapObj(ty, svMap)
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
  def pop(addr: Addr, idx: Value): (Value, Heap) = this(addr) match {
    case l: ListObj =>
      val (value, newList) = l.pop(idx)
      (value, copy(map = map + (addr -> newList)))
    case _ => error("Pop applied to non ListObj object")
  }
}
object Heap { def apply(seq: (Addr, Obj)*): Heap = Heap(seq.toMap) }
