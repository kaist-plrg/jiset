package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.UIdGen
import scala.collection.mutable.{ Map => MMap }

// IR States
case class State(
  var cursorGen: CursorGen[_ <: Cursor] = InstCursor,
  var context: Context = Context(),
  var ctxtStack: List[Context] = Nil,
  val globals: MMap[Id, Value] = MMap(),
  val heap: Heap = Heap(),
  var fnameOpt: Option[String] = None
) extends IRComponent {
  // move the cursor
  def moveTo(program: Program): State = moveTo(program.insts)
  def moveTo(insts: List[Inst]): State = moveTo(ISeq(insts))
  def moveTo(inst: Inst): State = { context.cursorOpt = cursorGen(inst); this }

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
    case RefValueProp(base, prop) => this(base, prop)
  }
  def apply(x: Id): Value = directLookup(x) match {
    case Absent if context.isBuiltin => Undef
    case v => v
  }
  def apply(base: Value, prop: PureValue): Value = base match {
    case comp: CompValue => prop match {
      case Str("Type") => comp.ty
      case Str("Value") => comp.value
      case Str("Target") => comp.target
      case _ => this(comp.escaped, prop)
    }
    case addr: Addr => heap(addr, prop)
    case Str(str) => this(str, prop)
    case v => error(s"not a proper reference base: ${v.beautified}")
  }
  def apply(str: String, prop: PureValue): PureValue = prop match {
    case Str("length") => INum(str.length)
    case INum(k) => Str(str(k.toInt).toString)
    case Num(k) => Str(str(k.toInt).toString)
    case v => error(s"wrong access of string reference: $str.${prop.beautified}")
  }
  def apply(addr: Addr): Obj = heap(addr)

  // setters
  def update(refV: RefValue, value: Value): this.type = refV match {
    case RefValueId(x) =>
      update(x, value); this
    case RefValueProp(base, prop) => base.escaped match {
      case addr: Addr =>
        update(addr, prop, value); this
      case _ =>
        error(s"illegal reference update: ${refV.beautified} = ${value.beautified}")
    }
  }
  def update(x: Id, value: Value): this.type = {
    if (locals contains x) locals += x -> value
    else if (globals contains x) globals += x -> value
    else error(s"illegal variable update: ${x.beautified} = ${value.beautified}")
    this
  }
  def update(addr: Addr, prop: PureValue, value: Value): this.type =
    { heap.update(addr, prop, value); this }

  // existence checks
  def exists(ref: RefValue): Bool = Bool(ref match {
    case RefValueId(id) =>
      val defined = globals.contains(id) || locals.contains(id)
      !defined || directLookup(id) == Absent
    case RefValueProp(base, prop) =>
      this(base.escaped, prop) == Absent
  })

  // delete a property from a map
  def delete(refV: RefValue): this.type = refV match {
    case RefValueId(x) =>
      error(s"cannot delete variable ${x.beautified}")
    case RefValueProp(base, prop) => base.escaped match {
      case addr: Addr =>
        heap.delete(addr, prop); this
      case _ =>
        error(s"illegal reference delete: delete ${refV.beautified}")
    }
  }

  // object operators
  def append(addr: Addr, value: PureValue): this.type =
    { heap.append(addr, value); this }
  def prepend(addr: Addr, value: PureValue): this.type =
    { heap.prepend(addr, value); this }
  def pop(addr: Addr, idx: PureValue): PureValue = heap.pop(addr, idx)
  def copyObj(addr: Addr): Addr = heap.copyObj(addr)
  def keys(addr: Addr, intSorted: Boolean): Addr = heap.keys(addr, intSorted)
  def allocMap(ty: Ty, map: Map[PureValue, PureValue] = Map()): Addr = heap.allocMap(ty, map)
  def allocList(list: List[PureValue]): Addr = heap.allocList(list)
  def allocSymbol(desc: PureValue): Addr = heap.allocSymbol(desc)
  def setType(addr: Addr, ty: Ty): this.type =
    { heap.setType(addr, ty); this }

  // get string for a given address
  def getString(value: Value): String = value match {
    case comp: CompValue => comp.beautified + (comp.value match {
      case addr: Addr => " -> " + heap(addr).beautified
      case _ => ""
    })
    case addr: Addr => addr.beautified + " -> " + heap(addr).beautified
    case _ => value.beautified
  }

  // copied
  def copied: State = {
    val newContext = context.copied
    val newCtxtStack = ctxtStack.map(_.copied)
    val newGlobals = MMap.from(globals)
    val newHeap = heap.copied
    State(cursorGen, newContext, newCtxtStack, newGlobals, newHeap, fnameOpt)
  }

  // move to the next cursor
  def moveNext: Unit = context.moveNext
}
