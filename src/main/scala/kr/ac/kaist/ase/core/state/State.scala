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
  // getters
  def apply(addr: Addr): Obj = heap(addr)
  def apply(refV: RefValue): (Value, State) = refV match {
    case RefValueId(id) =>
      (locals.getOrElse(id, globals.getOrElse(id, Absent)), this)
    case RefValueProp(addr, value) =>
      (heap(addr, value), this)
    case RefValueAST(astV, name) =>
      val ASTVal(ast) = astV
      ast.semantics(name) match {
        case Some((Func(fname, params, varparam, body), lst)) =>
          val (locals, rest) = ((Map[Id, Value](), params) /: (astV :: lst)) {
            case ((map, param :: rest), arg) =>
              (map + (param -> arg), rest)
            case (pair, _) => pair
          }
          rest match {
            case Nil =>
              val newSt = Interp.fixpoint(copy(context = fname, insts = List(body), locals = locals))
              (newSt.retValue.getOrElse(Absent), copy(heap = newSt.heap, globals = newSt.globals))
            case _ =>
              (ASTMethod(Func(fname, rest, varparam, body), locals), this)
          }
        case None => ast.subs(name) match {
          case Some(v) => (v, this)
          case None => error(s"Unexpected semantics: ${ast.name}.$name")
        }
      }
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
    if (id.name.startsWith("GLOBAL_")) copy(globals = globals + (id -> value))
    else copy(locals = locals + (id -> value))
  def updated(addr: Addr, key: Value, value: Value): State =
    copy(heap = heap.updated(addr, key, value))

  // deletes
  def deleted(refV: RefValue): State = refV match {
    case RefValueId(id) =>
      copy(locals = locals - id)
    case RefValueProp(addr, prop) =>
      copy(heap = heap.deleted(addr, prop))
    case _ => error(s"illegal reference delete: delete $refV")
  }

  // appends
  def append(addr: Addr, value: Value): State = {
    copy(heap = heap.append(addr, value))
  }

  // prepends
  def prepend(addr: Addr, value: Value): State = {
    copy(heap = heap.prepend(addr, value))
  }

  // pops
  def pop(addr: Addr, idx: Value): (Value, State) = {
    val (value, newHeap) = heap.pop(addr, idx)
    (value, copy(heap = newHeap))
  }

  // copy objects
  def copyObj(addr: Addr): (Addr, State) = {
    val (newAddr, newHeap) = heap.copyObj(addr)
    (newAddr, copy(heap = newHeap))
  }

  // keys of map
  def keys(addr: Addr): (Addr, State) = {
    val (newAddr, newHeap) = heap.keys(addr)
    (newAddr, copy(heap = newHeap))
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

  // symbol allocations
  def allocSymbol(desc: String): (Addr, State) = {
    val (newAddr, newHeap) = heap.allocSymbol(desc)
    (newAddr, copy(heap = newHeap))
  }
}
