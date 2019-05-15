package kr.ac.kaist.ase.core

// CORE States
case class State(
    insts: List[Inst],
    globals: Map[Id, Value],
    env: Env,
    heap: Heap
) extends CoreNode {
  // existence check
  def contains(prop: Prop): Boolean = prop match {
    case GlobalId(id) =>
      globals.contains(id)
    case PropId(addr, id) =>
      heap.contains(addr, id)
    case PropStr(addr, str) =>
      heap.contains(addr, str)
  }

  // getters
  def apply(prop: Prop): Value = prop match {
    case GlobalId(id) =>
      globals.getOrElse(id, error(s"free identifier: $id"))
    case PropId(addr, id) =>
      heap(addr, id)
    case PropStr(addr, str) =>
      heap(addr, str)
  }

  // setters
  def updated(prop: Prop, value: Value): State = prop match {
    case GlobalId(id) =>
      copy(globals = globals + (id -> value))
    case PropId(addr, id) =>
      updated(addr, id, value)
    case PropStr(addr, str) =>
      updated(addr, str, value)
  }
  def updated(addr: Addr, id: Id, value: Value): State =
    copy(heap = heap.updated(addr, id, value))
  def updated(addr: Addr, str: String, value: Value): State =
    copy(heap = heap.updated(addr, str, value))

  // deletes
  def deleted(prop: Prop): State = prop match {
    case GlobalId(id) =>
      copy(globals = globals - id)
    case PropId(addr, id) =>
      copy(heap = heap.deleted(addr, id))
    case PropStr(addr, str) =>
      copy(heap = heap.deleted(addr, str))
  }

  // object allocations
  def alloc(ty: Ty): (Addr, State) = alloc(ty, Map(), Map())
  def alloc(ty: Ty, idMap: Map[Id, Value], strMap: Map[String, Value]): (Addr, State) = {
    val (newAddr, newHeap) = heap.alloc(ty, idMap, strMap)
    (newAddr, copy(heap = newHeap))
  }

  // environment allocations
  def allocLocals(idMap: Map[Id, Value]): (Addr, State) = {
    val (newAddr, newHeap) = heap.allocLocals(idMap)
    (newAddr, copy(heap = newHeap))
  }

  // continue with continuations
  def continue(cont: Cont, value: Value): State = {
    val retSt = copy(insts = cont.insts, env = cont.env)
    retSt.updated(cont.prop, value)
  }
}
