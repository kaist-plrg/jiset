package kr.ac.kaist.ase.core

// CORE States
case class State(
    context: String = "<top-level>",
    retValue: Option[Value] = None,
    insts: List[Inst] = Nil,
    globals: Map[Id, Value] = Map(),
    locals: Map[Id, Value] = Map(),
    heap: Heap = Heap()
) extends CoreNode {
  // existence check
  def contains(refV: RefValue): Boolean = refV match {
    case RefValueId(id) =>
      (locals contains id) || (globals contains id)
    case RefValueProp(addr, value) =>
      heap.contains(addr, value)
    case _ => error(s"illegal contains check: contains $refV")
  }

  // getters
  def apply(refV: RefValue): (Value, State) = refV match {
    case RefValueId(id) =>
      (locals.getOrElse(id, globals.getOrElse(id, Absent)), this)
    case RefValueProp(addr, value) =>
      (heap(addr, value), this)
    case RefValueAST(astV, name) =>
      val ASTVal(ast) = astV
      val (Func(fname, params, body), lst) = ast.semantics(name)
      val (locals, _) = ((Map[Id, Value](), lst) /: params) {
        case ((map, arg :: rest), param) =>
          (map + (param -> arg), rest)
        case (pair, _) => pair
      }
      val newSt = Interp.fixpoint(copy(context = fname, insts = List(body), locals = locals))
      newSt.retValue match {
        case Some(v) => (v, copy(heap = newSt.heap, globals = newSt.globals))
        case None => error(s"no return value")
      }
    case RefValueToNumber(s) => (Num(s.toDouble), this)
  }

  // initialize local variables
  def define(id: Id, value: Value): State = copy(locals = locals + (id -> value))

  // setters
  def updated(refV: RefValue, value: Value): State = refV match {
    case RefValueId(id) => updated(id, value)
    case RefValueProp(addr, key) => updated(addr, key, value)
    case _ => error(s"illegal reference update: $refV = $value")
  }
  def updated(id: Id, value: Value): State =
    if (locals contains id) copy(locals = locals + (id -> value))
    else copy(globals = globals + (id -> value))
  def updated(addr: Addr, key: Value, value: Value): State =
    copy(heap = heap.updated(addr, key, value))

  // deletes
  def deleted(refV: RefValue): State = refV match {
    case RefValueId(id) =>
      if (locals contains id) copy(locals = locals - id)
      else copy(globals = globals - id)
    case RefValueProp(addr, prop) =>
      copy(heap = heap.deleted(addr, prop))
    case _ => error(s"illegal reference delete: delete $refV")
  }

  // pushses
  def push(addr: Addr, value: Value): State = {
    copy(heap = heap.push(addr, value))
  }

  // pops
  def pop(addr: Addr): (Value, State) = {
    val (value, newHeap) = heap.pop(addr)
    (value, copy(heap = newHeap))
  }

  // map allocations
  def allocMap(ty: Ty): (Addr, State) = allocMap(ty, Map())
  def allocMap(ty: Ty, map: Map[Value, Value]): (Addr, State) = {
    val (newAddr, newHeap) = heap.allocMap(ty, map)
    (newAddr, copy(heap = newHeap))
  }

  // list allocations
  def allocList(list: List[Value]): (Addr, State) = {
    val (newAddr, newHeap) = heap.allocList(list)
    (newAddr, copy(heap = newHeap))
  }
}
