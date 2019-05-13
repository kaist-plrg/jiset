package kr.ac.kaist.ase.node.core

// CORE Heaps
case class Heap(
    map: Map[Addr, Obj] = Map(),
    size: Int = 0
) extends CoreNode {
  // existence check
  def contains(addr: Addr): Boolean = map contains addr
  def contains(addr: Addr, id: Id): Boolean = this(addr) contains id
  def contains(addr: Addr, str: String): Boolean = this(addr) contains str

  // getters
  def apply(addr: Addr): Obj = map.getOrElse(addr, error(s"unknown address: ${beautify(addr)}"))
  def apply(addr: Addr, id: Id): Value = this(addr)(id)
  def apply(addr: Addr, str: String): Value = this(addr)(str)

  // setters
  def updated(addr: Addr, id: Id, value: Value): Heap =
    copy(map = map + (addr -> this(addr).updated(id, value)))
  def updated(addr: Addr, str: String, value: Value): Heap =
    copy(map = map + (addr -> this(addr).updated(str, value)))

  // deletes
  def deleted(addr: Addr, id: Id): Heap =
    copy(map = map + (addr -> this(addr).deleted(id)))
  def deleted(addr: Addr, str: String): Heap =
    copy(map = map + (addr -> this(addr).deleted(str)))

  // object allocations
  def alloc(
    ty: Ty,
    idMap: Map[Id, Value] = Map(),
    strMap: Map[String, Value] = Map()
  ): (Addr, Heap) = {
    val newAddr = DynamicAddr(size)
    val newMap = map + (newAddr -> Obj(ty, idMap, strMap))
    val newSize = size + 1
    (newAddr, Heap(newMap, newSize))
  }

  // environment allocations
  def allocLocals(idMap: Map[Id, Value] = Map()): (Addr, Heap) =
    alloc(Heap.ENV_TYPE, idMap)
}
object Heap {
  val ENV_TYPE: Ty = Ty("CoreEnv")
}
