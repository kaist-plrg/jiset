package kr.ac.kaist.jiset.js

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import scala.collection.mutable.{ Map => MMap }

// initialize states
object Initialize {
  def apply(
    script: Script,
    filename: String = "unknown",
    cursorGen: CursorGen[_ <: Cursor] = InstCursor
  ): State = {
    val Script0(bodyOpt, _, _) = script
    val runJobsAlgo = algos("RunJobs")
    initState(
      cursorGen = cursorGen,
      inst = if (bodyOpt.isDefined) runJobsAlgo.body else ISeq(Nil),
      bodyOpt = bodyOpt,
      filename = filename,
    )
  }

  // initial state
  def initState(
    cursorGen: CursorGen[_ <: Cursor],
    inst: Inst,
    bodyOpt: Option[ScriptBody],
    filename: String
  ): State = State(
    cursorGen = cursorGen,
    context = Context(cursorOpt = cursorGen(inst)),
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

  // get length value from origParams
  def getLength(params: List[Param]): Int = {
    import Param.Kind._
    params.count(_.kind match {
      case Normal => true
      case _ => false
    })
  }

  // add builtin object
  def addBuiltin(map: MMap[Addr, Obj]): Unit = for {
    (_, algo) <- algos
    head <- algo.head match {
      case head: BuiltinHead if head.name != "GLOBAL.AsyncFunction" => Some(head)
      case _ => None
    }
    (base, prop, propV, propName) <- head.ref match {
      case RefId(Id(name)) =>
        map.getOrElse(NamedAddr(s"GLOBAL.$name"), None) match {
          case IRNotSupported(tyname, desc) => None
          case _ => Some(GLOBAL, name, Str(name), name)
        }
      case RefProp(ref, EStr(prop)) =>
        Some(GLOBAL + "." + ref.beautified, prop, Str(prop), prop)
      case RefProp(ref, ERef(RefId(Id(name)))) if name startsWith SYMBOL_PREFIX =>
        val symbolName = name.substring(SYMBOL_PREFIX.length)
        val symbolAddr = NamedAddr(GLOBAL + ".Symbol." + symbolName)
        Some(GLOBAL + "." + ref.beautified, name, symbolAddr, s"[Symbol.$symbolName]")
      case _ => None
    }
    baseAddr = NamedAddr(s"$base.SubMap")
    irMap <- map.get(baseAddr) match {
      case Some(m: IRMap) => Some(m)
      case _ => None
    }
    name <- propV match {
      case Str(name) => Some(s"$base.$prop")
      case NamedAddr(name) => Some(s"$base[$prop]")
      case _ => None
    }
    addr = NamedAddr(s"$name")
    descAddr = NamedAddr(s"DESC:$name")
  } {
    irMap.update(propV, descAddr)
    map.getOrElse(descAddr, map += descAddr -> IRMap("PropertyDescriptor")(List(
      Str("Value") -> addr,
      Str("Writable") -> Bool(true),
      Str("Enumerable") -> Bool(false),
      Str("Configurable") -> Bool(true),
    )))

    val irrMap = map.get(addr) match {
      case Some(m: IRMap) => m
      case _ => IRMap("BuiltinFunctionObject")(Nil)
    }
    irrMap
      .findOrUpdate(Str("Extensible"), Bool(true))
      .findOrUpdate(Str("ScriptOrModule"), Null)
      .findOrUpdate(Str("Realm"), NamedAddr("REALM"))
      .findOrUpdate(Str("Code"), Func(algo))
      .findOrUpdate(Str("Prototype"), NamedAddr("GLOBAL.Function.prototype"))
      .findOrUpdate(Str("SubMap"), NamedAddr(s"$name.SubMap"))
    map += addr -> irrMap

    val subAddr = NamedAddr(s"$name.SubMap")
    val nameAddr = NamedAddr(s"DESC:$name.name")
    val lengthAddr = NamedAddr(s"DESC:$name.length")

    val subMap = map.get(subAddr) match {
      case Some(m: IRMap) => m
      case _ => IRMap("SubMap")(Nil)
    }
    subMap
      .findOrUpdate(Str("name"), NamedAddr(s"DESC:$name.name"))
      .findOrUpdate(Str("length"), NamedAddr(s"DESC:$name.length"))
    map += subAddr -> subMap

    val nameMap = map.get(nameAddr) match {
      case Some(m: IRMap) => m
      case _ => IRMap("PropertyDescriptor")(Nil)
    }
    nameMap
      .findOrUpdate(Str("Value"), Str(propName))
      .findOrUpdate(Str("Writable"), Bool(false))
      .findOrUpdate(Str("Enumerable"), Bool(false))
      .findOrUpdate(Str("Configurable"), Bool(true))
    map += nameAddr -> nameMap

    val lengthMap = map.get(lengthAddr) match {
      case Some(m: IRMap) => m
      case _ => IRMap("PropertyDescriptor")(Nil)
    }
    lengthMap
      .findOrUpdate(Str("Value"), Num(getLength(head.origParams)))
      .findOrUpdate(Str("Writable"), Bool(false))
      .findOrUpdate(Str("Enumerable"), Bool(false))
      .findOrUpdate(Str("Configurable"), Bool(true))
    map += lengthAddr -> lengthMap
  }
}
