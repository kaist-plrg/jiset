package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.ast.ScriptBody
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import scala.collection.mutable.{ Map => MMap }

// initialize states
object Initialize {
  // initial state
  def apply(
    inst: Inst = Inst(s"app $RESULT = (RunJobs)"),
    bodyOpt: Option[ScriptBody] = None,
    filename: String = ""
  ): State = initState(inst, bodyOpt, filename)
  def initState(
    inst: Inst,
    bodyOpt: Option[ScriptBody],
    filename: String
  ): State = State(
    context = Context(insts = List(inst)),
    ctxtStack = Nil,
    globals = initGlobal(bodyOpt, filename),
    heap = initHeap,
  )

  // initial global variables
  def initGlobal(
    bodyOpt: Option[ScriptBody],
    filename: String
  ): MMap[Id, Value] = {
    val map = MMap[Id, Value]()
    bodyOpt.map(body => map += Id(SCRIPT_BODY) -> ASTVal(body))
    map += Id(FILENAME) -> Str(filename)
    for (c <- consts) {
      map += Id(CONST_PREFIX + c) -> NamedAddr(CONST_PREFIX + c)
    }
    for (i <- intrinsics) {
      map += Id(INTRINSIC_PREFIX + i) -> intrinsicToAddr(i)
    }
    for (s <- symbols) {
      map += Id(SYMBOL_PREFIX + s) -> NamedAddr(s"$GLOBAL.Symbol.$s")
    }
    for ((x, algo) <- algos if algo.isNormal) {
      map += Id(x) -> Func(algo)
    }
    for ((name, value) <- BaseModel.globals) {
      map += Id(name) -> value
    }
    map
  }

  // initial heap
  def initHeap: Heap = {
    val map = MMap[Addr, Obj]()
    for (c <- consts) {
      map += NamedAddr(CONST_PREFIX + c) -> IRSymbol(Str(CONST_PREFIX + c))
    }
    for (s <- symbols) {
      map += NamedAddr(s"$GLOBAL.Symbol.$s") -> IRSymbol(Str("Symbol." + s))
    }
    for ((addr, obj) <- BaseModel.heap) {
      map += addr -> obj.copied
    }
    addBuiltin(map)
    Heap(map)
  }

  // add builtin object
  def addBuiltin(map: MMap[Addr, Obj]): Unit = for {
    (_, algo) <- algos
    head <- algo.head match {
      case head: BuiltinHead => Some(head)
      case _ => None
    }
    (base, prop, propV, propName) <- head.ref match {
      case RefId(Id(name)) if name.head.isLower =>
        Some(GLOBAL, name, Str(name), name)
      case RefProp(ref, EStr(prop)) =>
        Some(GLOBAL + "." + ref.beautified, prop, Str(prop), prop)
      case RefProp(ref, ERef(RefId(Id(name)))) if name startsWith SYMBOL_PREFIX =>
        val symbolName = name.substring(SYMBOL_PREFIX.length)
        Some(GLOBAL + "." + ref.beautified, name, NamedAddr(name), s"[Symbol.$symbolName]")
      case _ => None
    }
    baseAddr = NamedAddr(s"$base.SubMap")
    irMap <- map.get(baseAddr) match {
      case Some(m: IRMap) => Some(m)
      case _ => None
    }
    name <- propV match {
      case Str(name) => Some(s"$base.$prop")
      case NamedAddr(name) => Some(s"$base[$name]")
      case _ => None
    }
    addr = NamedAddr(s"$name")
    descAddr = NamedAddr(s"DESC:$name")
  } {
    irMap.update(propV, descAddr)
    map += descAddr -> IRMap("PropertyDescriptor")(List(
      Str("Value") -> addr,
      Str("Writable") -> Bool(true),
      Str("Enumerable") -> Bool(false),
      Str("Configurable") -> Bool(true),
    ))
    map.get(addr) match {
      case Some(irMap: IRMap) => {
        irMap.update(Str("Extensible"), Bool(true))
        irMap.update(Str("ScriptOrModule"), Null)
        irMap.update(Str("Realm"), NamedAddr("REALM"))
      }
      case _ => map += addr -> IRMap("BuiltinFunctionObject")(List(
        Str("Code") -> Func(algo),
        Str("Prototype") -> NamedAddr("GLOBAL.Function.prototype"),
        Str("Extensible") -> Bool(true),
        Str("ScriptOrModule") -> Null,
        Str("Realm") -> NamedAddr("REALM"),
        Str("SubMap") -> NamedAddr(s"$name.SubMap"),
      ))
    }
    map += NamedAddr(s"$name.SubMap") -> IRMap("SubMap")(List(
      Str("name") -> NamedAddr(s"DESC:$name.name"),
      Str("length") -> NamedAddr(s"DESC:$name.length"),
    ))
    map += NamedAddr(s"DESC:$name.name") -> IRMap("PropertyDescriptor")(List(
      Str("Value") -> Str(propName),
      Str("Writable") -> Bool(false),
      Str("Enumerable") -> Bool(false),
      Str("Configurable") -> Bool(true),
    ))
    map += NamedAddr(s"DESC:$name.length") -> IRMap("PropertyDescriptor")(List(
      Str("Value") -> Num(head.origParams.length),
      Str("Writable") -> Bool(false),
      Str("Enumerable") -> Bool(false),
      Str("Configurable") -> Bool(true),
    ))
  }
}
