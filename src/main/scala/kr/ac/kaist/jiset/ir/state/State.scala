package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.UIdGen
import scala.collection.mutable.{ Map => MMap }

// IR States
case class State(
  val cursorGen: CursorGen[_ <: Cursor] = InstCursor,
  var context: Context = Context(),
  var ctxtStack: List[Context] = Nil,
  val globals: MMap[Id, Value] = MMap(),
  val heap: Heap = Heap()
) extends IRComponent {
  // return id and its value
  def retId: Id = context.retId

  // get local variable maps
  def locals: MMap[Id, Value] = context.locals

  // lookup variable directly
  def directLookup(x: Id): Value =
    locals.getOrElse(x, globals.getOrElse(x, error(s"unknown variable: ${x.name}")))

  // getters
  def apply(refV: RefValue): Value = refV match {
    case RefValueId(x) => this(x)
    case RefValueProp(addr, value) => this(addr, value)
    case RefValueString(str, value) => this(str, value)
  }
  def apply(x: Id): Value = directLookup(x) match {
    case Absent if context.isBuiltin => Undef
    case v => v
  }
  def apply(addr: Addr, key: Value): Value = heap(addr, key)
  def apply(str: String, key: Value): Value = key match {
    case Str("length") => INum(str.length)
    case INum(k) => Str(str(k.toInt).toString)
    case Num(k) => Str(str(k.toInt).toString)
    case v => error(s"wrong access of string reference: $str.${key.beautified}")
  }
  def apply(addr: Addr): Obj = heap(addr)

  // setters
  def update(refV: RefValue, value: Value): this.type = refV match {
    case RefValueId(x) =>
      update(x, value); this
    case RefValueProp(addr, key) =>
      update(addr, key, value); this
    case _ => error(s"illegal reference update: ${refV.beautified} = ${value.beautified}")
  }
  def update(x: Id, value: Value): this.type = {
    if (locals contains x) locals += x -> value
    else if (globals contains x) globals += x -> value
    else error(s"illegal variable update: ${x.beautified} = ${value.beautified}")
    this
  }
  def update(addr: Addr, key: Value, value: Value): this.type =
    { heap.update(addr, key, value); this }

  // delete a key from a map
  def delete(refV: RefValue): this.type = refV match {
    case RefValueId(x) =>
      context.locals -= x; this
    case RefValueProp(addr, prop) =>
      heap.delete(addr, prop); this
    case _ => error(s"illegal reference delete: delete ${refV.beautified}")
  }

  // object operators
  def append(addr: Addr, value: Value): this.type =
    { heap.append(addr, value); this }
  def prepend(addr: Addr, value: Value): this.type =
    { heap.prepend(addr, value); this }
  def pop(addr: Addr, idx: Value): Value = heap.pop(addr, idx)
  def copyObj(addr: Addr): Addr = heap.copyObj(addr)
  def keys(addr: Addr, intSorted: Boolean): Addr = heap.keys(addr, intSorted)
  def allocMap(ty: Ty, map: Map[Value, Value] = Map()): Addr = heap.allocMap(ty, map)
  def allocList(list: List[Value]): Addr = heap.allocList(list)
  def allocSymbol(desc: Value): Addr = heap.allocSymbol(desc)
  def setType(addr: Addr, ty: Ty): this.type =
    { heap.setType(addr, ty); this }

  // get string for a given address
  def getString(value: Value): String = value match {
    case addr: Addr => addr.beautified + " -> " + heap(addr).beautified
    case _ => value.beautified
  }

  // copied
  def copied: State = {
    val newContext = context.copied
    val newCtxtStack = ctxtStack.map(_.copied)
    val newGlobals = MMap.from(globals)
    val newHeap = heap.copied
    State(cursorGen, newContext, newCtxtStack, newGlobals, newHeap)
  }

  // move to the next cursor
  def moveNext: Unit = context.cursorOpt = context.cursorOpt.flatMap(_.next)
}
object State {
  def apply[T <: Cursor](cursorGen: CursorGen[T]) = new Generator(cursorGen)
  class Generator[T <: Cursor](cursorGen: CursorGen[T]) {
    def apply(program: Program): State = apply(program.insts)
    def apply(insts: List[Inst]): State = apply(ISeq(insts))
    def apply(inst: Inst): State = State(
      cursorGen = cursorGen,
      context = Context(cursorOpt = Some(InstCursor(inst))),
    )
  }
}
