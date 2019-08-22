package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.model.ModelHelper._

object BuiltinHeap {
  def get: Map[Addr, Obj] = (Map[Addr, Obj]() /: mapInfo) {
    case (m, ("GLOBAL", Struct(ty, _, nmap))) => ((m ++ Map(
      NamedAddr("GLOBAL") -> CoreMap(Ty("Map"), nmap.map.map {
        case (k, _) => k -> NamedAddr("DESC:GLOBAL" + getPropStr(k))
      })
    )) /: nmap.map) { case (m, (k, prop)) => addDesc("GLOBAL", m, k, prop) }
    case (m, (name, Struct(ty, imap, nmap))) => ((m ++ Map(
      NamedAddr(s"$name") -> CoreMap(
        Ty(ty),
        Model.tyMap.getOrElse(ty, Map()) ++
          imap.map +
          (Str("SubMap") -> NamedAddr(s"$name.SubMap"))
      ),
      NamedAddr(s"$name.SubMap") -> CoreMap(Ty("SubMap"), nmap.map.map {
        case (k, _) => k -> NamedAddr("DESC:" + name + getPropStr(k))
      })
    )) /: nmap.map) { case (m, (k, prop)) => addDesc(name, m, k, prop) }
  } ++ (singletonInfo.map {
    case name => (NamedAddr(SYMBOL_PREFIX + name) -> CoreSymbol(Str("Symbol." + name)))
  }.toMap) ++ (notSupportedInfo.map {
    case name => (NamedAddr("GLOBAL." + name) -> CoreNotSupported(name))
  }.toMap)

  val builtinMethods: List[(String, Int, Func)] = List(
    ("GLOBAL.String.prototype.toUpperCase", 0, Func("", List(), None, IExpr(ENotSupported("toUpperCase")))),
    ("GLOBAL.String.prototype.toLocaleLowerCase", 0, Func("", List(), None, IExpr(ENotSupported("toLocaleLowerCase")))),
    ("GLOBAL.String.prototype.toLocaleUpperCase", 0, Func("", List(), None, IExpr(ENotSupported("toLocaleUpperCase")))),
    ("GLOBAL.Number.prototype.toLocaleString", 0, Func("", List(), None, IExpr(ENotSupported("toLocaleString")))),
    ("GLOBAL.Array.prototype.sort", 1, Func("", List(), None, IExpr(ENotSupported("sort")))),
    ("GLOBAL.INTRINSIC_ThrowTypeError", 0, INTRINSIC_ThrowTypeError.func)
  )

  private def addDesc(
    name: String,
    m: Map[Addr, Obj],
    k: Value,
    prop: Property
  ): Map[Addr, Obj] = prop match {
    case DataProperty(v, w, e, c) => m + (
      NamedAddr(s"DESC:$name${getPropStr(k)}") ->
      CoreMap(Ty("PropertyDescriptor"), Map(
        Str("Value") -> v,
        Str("Writable") -> Bool(w),
        Str("Enumerable") -> Bool(e),
        Str("Configurable") -> Bool(c)
      ))
    )
    case AccessorProperty(g, s, e, c) => m + (
      NamedAddr(s"DESC:$name${getPropStr(k)}") ->
      CoreMap(Ty("PropertyDescriptor"), Map(
        Str("Get") -> g,
        Str("Set") -> s,
        Str("Enumerable") -> Bool(e),
        Str("Configurable") -> Bool(c)
      ))
    )
  }

  private case class Struct(
    typeName: String,
    imap: IMap,
    nmap: NMap
  )

  private case class IMap(map: Map[Value, Value])
  private object IMap {
    def apply(pairs: (String, Value)*): IMap = IMap(
      pairs.map { case (s, v) => Str(s) -> v }.toMap[Value, Value]
    )
  }

  private case class NMap(map: Map[Value, Property]) {
    def ++(aMap: Map[Value, Property]): NMap = NMap(map ++ aMap)
  }
  private object NMap {
    def apply(pairs: (String, Property)*): NMap = NMap(
      pairs.map { case (s, p) => Str(s) -> p }.toMap[Value, Property]
    )
  }

  private trait Property
  private case class DataProperty(
    value: Value,
    writable: Boolean,
    enumerable: Boolean,
    configurable: Boolean
  ) extends Property
  private case class AccessorProperty(
    get: Value,
    set: Value,
    enumerable: Boolean,
    configurable: Boolean
  ) extends Property
  private val T = true
  private val F = false
  private val U = Undef

  private val errList: List[(String, Func)] = List(
    ("EvalError", GLOBALDOTEvalError.func),
    ("RangeError", GLOBALDOTRangeError.func),
    ("ReferenceError", GLOBALDOTReferenceError.func),
    ("SyntaxError", GLOBALDOTSyntaxError.func),
    ("TypeError", GLOBALDOTTypeError.func),
    ("URIError", GLOBALDOTURIError.func)
  )

  private def getErrMap(errName: String, errFunc: Func): Map[String, Struct] = Map(
    s"GLOBAL.$errName" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Error"),
        "Code" -> errFunc
      ),
      nmap = NMap(
        "name" -> DataProperty(Str(errName), T, F, T),
        "prototype" -> DataProperty(NamedAddr(s"GLOBAL.$errName.prototype"), F, F, F)
      )
    ),
    s"GLOBAL.$errName.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Error.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr(s"GLOBAL.$errName"), T, F, T),
        "message" -> DataProperty(Str(""), T, F, T),
        "name" -> DataProperty(Str(errName), T, F, T)
      )
    )
  )

  private val mapInfo: Map[String, Struct] = (Map(
    "REALM" -> Struct(
      typeName = "RealmRecord",
      imap = IMap(),
      nmap = NMap()
    ),

    "GLOBAL" -> Struct(
      typeName = "Map",
      imap = IMap(),
      nmap = NMap(
        "print" -> DataProperty(NamedAddr("GLOBAL.print"), T, F, T),
        "$262" -> DataProperty(NamedAddr("GLOBAL.$262"), T, F, T),
        "Infinity" -> DataProperty(Num(Double.PositiveInfinity), F, F, F),
        "NaN" -> DataProperty(Num(Double.NaN), F, F, F),
        "undefined" -> DataProperty(Undef, F, F, F),
        "Array" -> DataProperty(NamedAddr("GLOBAL.Array"), T, F, T),
        "ArrayBuffer" -> DataProperty(NamedAddr("GLOBAL.ArrayBuffer"), T, F, T),
        "Boolean" -> DataProperty(NamedAddr("GLOBAL.Boolean"), T, F, T),
        "DataView" -> DataProperty(NamedAddr("GLOBAL.DataView"), T, F, T),
        "Date" -> DataProperty(NamedAddr("GLOBAL.Date"), T, F, T),
        "Error" -> DataProperty(NamedAddr("GLOBAL.Error"), T, F, T),
        "EvalError" -> DataProperty(NamedAddr("GLOBAL.EvalError"), T, F, T),
        "Float32Array" -> DataProperty(NamedAddr("GLOBAL.Float32Array"), T, F, T),
        "Float64Array" -> DataProperty(NamedAddr("GLOBAL.Float64Array"), T, F, T),
        "Function" -> DataProperty(NamedAddr("GLOBAL.Function"), T, F, T),
        "Int8Array" -> DataProperty(NamedAddr("GLOBAL.Int8Array"), T, F, T),
        "Int16Array" -> DataProperty(NamedAddr("GLOBAL.Int16Array"), T, F, T),
        "Int32Array" -> DataProperty(NamedAddr("GLOBAL.Int32Array"), T, F, T),
        "Map" -> DataProperty(NamedAddr("GLOBAL.Map"), T, F, T),
        "Number" -> DataProperty(NamedAddr("GLOBAL.Number"), T, F, T),
        "Object" -> DataProperty(NamedAddr("GLOBAL.Object"), T, F, T),
        "Promise" -> DataProperty(NamedAddr("GLOBAL.Promise"), T, F, T),
        "Proxy" -> DataProperty(NamedAddr("GLOBAL.Proxy"), T, F, T),
        "RangeError" -> DataProperty(NamedAddr("GLOBAL.RangeError"), T, F, T),
        "ReferenceError" -> DataProperty(NamedAddr("GLOBAL.ReferenceError"), T, F, T),
        "RegExp" -> DataProperty(NamedAddr("GLOBAL.RegExp"), T, F, T),
        "Set" -> DataProperty(NamedAddr("GLOBAL.Set"), T, F, T),
        "SharedArrayBuffer" -> DataProperty(NamedAddr("GLOBAL.SharedArrayBuffer"), T, F, T),
        "String" -> DataProperty(NamedAddr("GLOBAL.String"), T, F, T),
        "Symbol" -> DataProperty(NamedAddr("GLOBAL.Symbol"), T, F, T),
        "SyntaxError" -> DataProperty(NamedAddr("GLOBAL.SyntaxError"), T, F, T),
        "TypeError" -> DataProperty(NamedAddr("GLOBAL.TypeError"), T, F, T),
        "Uint8Array" -> DataProperty(NamedAddr("GLOBAL.Uint8Array"), T, F, T),
        "Uint8ClampedArray" -> DataProperty(NamedAddr("GLOBAL.Uint8ClampedArray"), T, F, T),
        "Uint16Array" -> DataProperty(NamedAddr("GLOBAL.Uint16Array"), T, F, T),
        "Uint32Array" -> DataProperty(NamedAddr("GLOBAL.Uint32Array"), T, F, T),
        "URIError" -> DataProperty(NamedAddr("GLOBAL.URIError"), T, F, T),
        "WeakMap" -> DataProperty(NamedAddr("GLOBAL.WeakMap"), T, F, T),
        "WeakSet" -> DataProperty(NamedAddr("GLOBAL.WeakSet"), T, F, T),
        "Atomics" -> DataProperty(NamedAddr("GLOBAL.Atomics"), T, F, T),
        "JSON" -> DataProperty(NamedAddr("GLOBAL.JSON"), T, F, T),
        "Math" -> DataProperty(NamedAddr("GLOBAL.Math"), T, F, T),
        "Reflect" -> DataProperty(NamedAddr("GLOBAL.Reflect"), T, F, T)
      )
    ),
    "GLOBAL.print" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> HostPrint.func
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(0.0), F, F, T)
      )
    ),
    "GLOBAL.Object" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTObject.func
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Object.prototype"), F, F, F)
      )
    ),
    "GLOBAL.Object.prototype" -> Struct(
      typeName = "ImmutablePrototypeExoticObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> Null
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.Object"), T, F, T)
      )
    ),
    "GLOBAL.Function" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTFunction.func
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Function.prototype"), F, F, F)
      )
    ),
    "GLOBAL.Function.prototype" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype"),
        "Code" -> Func("", List(), None, IReturn(EUndef))
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(0.0), F, F, T),
        "name" -> DataProperty(Str(""), F, F, T),
        "constructor" -> DataProperty(NamedAddr("GLOBAL.Function"), T, F, T),
        "caller" -> AccessorProperty(NamedAddr("GLOBAL.INTRINSIC_ThrowTypeError"), NamedAddr("GLOBAL.INTRINSIC_ThrowTypeError"), F, T),
        "arguments" -> AccessorProperty(NamedAddr("GLOBAL.INTRINSIC_ThrowTypeError"), NamedAddr("GLOBAL.INTRINSIC_ThrowTypeError"), F, T)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.hasInstance") -> DataProperty(NamedAddr("GLOBAL.Function.prototype[#GLOBAL.Symbol.hasInstance]"), F, F, F)
        )
    ),
    "GLOBAL.Function.prototype[#GLOBAL.Symbol.hasInstance]" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTFunctionDOTprototypeDOTSYMBOL_hasInstance.func
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T),
        "name" -> DataProperty(Str("[Symbol.hasInstance]"), F, F, T)
      )
    ),
    "GLOBAL.Boolean" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTBoolean.func
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Boolean.prototype"), F, F, F)
      )
    ),
    "GLOBAL.Boolean.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "BooleanData" -> Bool(false),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.Boolean"), T, F, T)
      )
    ),
    "GLOBAL.Symbol" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTSymbol.func
      ),
      nmap = NMap(
        "asyncIterator" -> DataProperty(NamedAddr("GLOBAL.Symbol.asyncIterator"), F, F, F),
        "hasInstance" -> DataProperty(NamedAddr("GLOBAL.Symbol.hasInstance"), F, F, F),
        "isConcatSpreadable" -> DataProperty(NamedAddr("GLOBAL.Symbol.isConcatSpreadable"), F, F, F),
        "iterator" -> DataProperty(NamedAddr("GLOBAL.Symbol.iterator"), F, F, F),
        "match" -> DataProperty(NamedAddr("GLOBAL.Symbol.match"), F, F, F),
        "replace" -> DataProperty(NamedAddr("GLOBAL.Symbol.replace"), F, F, F),
        "search" -> DataProperty(NamedAddr("GLOBAL.Symbol.search"), F, F, F),
        "species" -> DataProperty(NamedAddr("GLOBAL.Symbol.species"), F, F, F),
        "split" -> DataProperty(NamedAddr("GLOBAL.Symbol.split"), F, F, F),
        "toPrimitive" -> DataProperty(NamedAddr("GLOBAL.Symbol.toPrimitive"), F, F, F),
        "toStringTag" -> DataProperty(NamedAddr("GLOBAL.Symbol.toStringTag"), F, F, F),
        "unscopables" -> DataProperty(NamedAddr("GLOBAL.Symbol.unscopables"), F, F, F),
        "length" -> DataProperty(Num(0.0), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Symbol.prototype"), F, F, F)
      )
    ),
    "GLOBAL.Symbol.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.Symbol"), T, F, T)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("Symbol"), F, F, T)
        )
    ),
    "GLOBAL.Error" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTError.func
      ),
      nmap = NMap(
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Error.prototype"), F, F, F)
      )
    ),
    "GLOBAL.Error.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.Error"), T, F, T),
        "message" -> DataProperty(Str(""), T, F, T),
        "name" -> DataProperty(Str("Error"), T, F, T)
      )
    ),
    "GLOBAL.Number" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTNumber.func
      ),
      nmap = NMap(
        "EPSILON" -> DataProperty(Num(math.ulp(1.0)), F, F, F),
        "MAX_SAFE_INTEGER" -> DataProperty(INum(9007199254740991L), F, F, F),
        "MAX_VALUE" -> DataProperty(Num(Double.MaxValue), F, F, F),
        "MIN_SAFE_INTEGER" -> DataProperty(INum(-9007199254740991L), F, F, F),
        "MIN_VALUE" -> DataProperty(Num(Double.MinPositiveValue), F, F, F),
        "NaN" -> DataProperty(Num(Double.NaN), F, F, F),
        "NEGATIVE_INFINITY" -> DataProperty(Num(Double.NegativeInfinity), F, F, F),
        "parseFloat" -> DataProperty(NamedAddr("GLOBAL.parseFloat"), T, F, T),
        "parseInt" -> DataProperty(NamedAddr("GLOBAL.parseInt"), T, F, T),
        "POSITIVE_INFINITY" -> DataProperty(Num(Double.PositiveInfinity), F, F, F),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Number.prototype"), F, F, F)
      )
    ),
    "GLOBAL.Number.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "NumberData" -> Num(0.0),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.Number"), T, F, T)
      )
    ),
    "GLOBAL.String" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTString.func
      ),
      nmap = NMap(
        "prototype" -> DataProperty(NamedAddr("GLOBAL.String.prototype"), F, F, F)
      )
    ),
    "GLOBAL.String.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "StringData" -> Str(""),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(0.0), F, F, F),
        "constructor" -> DataProperty(NamedAddr("GLOBAL.String"), T, F, T)
      )
    ),
    "GLOBAL.INTRINSIC_StringIteratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.INTRINSIC_IteratorPrototype")
      ),
      nmap = NMap() ++ Map(
        NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("String Iterator"), F, F, T)
      )
    ),
    "GLOBAL.Array" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTArray.func
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Array.prototype"), F, F, F)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.species") -> AccessorProperty(NamedAddr("GLOBAL.Array.SYMBOL_species"), U, F, T)
        )
    ),
    "GLOBAL.Array.SYMBOL_species" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func("GLOBAL.Array.SYMBOL_species", List(Id("this")), None, IReturn(ERef(RefId(Id("this")))))
      ),
      nmap = NMap(
        "name" -> DataProperty(Str("get [Symbol.species]"), F, F, T),
        "length" -> DataProperty(Num(0.0), F, F, T)
      )
    ),
    "GLOBAL.Array.prototype" -> Struct(
      typeName = "ArrayExoticObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(0.0), T, F, F),
        "constructor" -> DataProperty(NamedAddr("GLOBAL.Array"), T, F, T)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.iterator") -> DataProperty(NamedAddr("GLOBAL.Array.prototype.values"), T, F, T),
          NamedAddr("GLOBAL.Symbol.unscopables") -> DataProperty(NamedAddr("GLOBAL.Array.prototype[#GLOBAL.Symbol.unscopables]"), F, F, T)
        )
    ),
    "GLOBAL.Array.prototype[#GLOBAL.Symbol.unscopables]" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true)
      ),
      nmap = NMap(
        "copyWithin" -> DataProperty(Bool(true), T, T, T),
        "entries" -> DataProperty(Bool(true), T, T, T),
        "fill" -> DataProperty(Bool(true), T, T, T),
        "find" -> DataProperty(Bool(true), T, T, T),
        "findIndex" -> DataProperty(Bool(true), T, T, T),
        "flat" -> DataProperty(Bool(true), T, T, T),
        "flatMap" -> DataProperty(Bool(true), T, T, T),
        "includes" -> DataProperty(Bool(true), T, T, T),
        "keys" -> DataProperty(Bool(true), T, T, T),
        "values" -> DataProperty(Bool(true), T, T, T)
      )
    ),
    "GLOBAL.INTRINSIC_ArrayIteratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.INTRINSIC_IteratorPrototype")
      ),
      nmap = NMap() ++ Map(
        NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("Array Iterator"), F, F, T)
      )
    ),
    "GLOBAL.Map" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTMap.func
      ),
      nmap = NMap(
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Map.prototype"), F, F, F)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.species") -> AccessorProperty(NamedAddr("GLOBAL.Map.SYMBOL_species"), U, F, T)
        )
    ),
    "GLOBAL.Map.SYMBOL_species" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func("GLOBAL.Map.SYMBOL_species", List(Id("this")), None, IReturn(ERef(RefId(Id("this")))))
      ),
      nmap = NMap(
        "name" -> DataProperty(Str("get [Symbol.species]"), F, F, T),
        "length" -> DataProperty(Num(0.0), F, F, T)
      )
    ),
    "GLOBAL.Map.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.Map"), T, F, T),
        "size" -> AccessorProperty(NamedAddr("GLOBAL.Map.prototype.size"), U, F, T)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.iterator") -> DataProperty(NamedAddr("GLOBAL.Map.prototype.entries"), T, F, T),
          NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("Map"), F, F, T)
        )
    ),
    "GLOBAL.Map.prototype.size" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTgetMapDOTprototypeDOTsize.func
      ),
      nmap = NMap(
        "name" -> DataProperty(Str("get size"), F, F, T),
        "length" -> DataProperty(Num(0.0), F, F, T)
      )
    ),
    "GLOBAL.INTRINSIC_MapIteratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.INTRINSIC_IteratorPrototype")
      ),
      nmap = NMap() ++ Map(
        NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("Map Iterator"), F, F, T)
      )
    ),
    "GLOBAL.Set" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTSet.func
      ),
      nmap = NMap(
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Set.prototype"), F, F, F)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.species") -> AccessorProperty(NamedAddr("GLOBAL.Set.SYMBOL_species"), U, F, T)
        )
    ),
    "GLOBAL.Set.SYMBOL_species" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func("GLOBAL.Set.SYMBOL_species", List(Id("this")), None, IReturn(ERef(RefId(Id("this")))))
      ),
      nmap = NMap(
        "name" -> DataProperty(Str("get [Symbol.species]"), F, F, T),
        "length" -> DataProperty(Num(0.0), F, F, T)
      )
    ),
    "GLOBAL.Set.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.Set"), T, F, T),
        "keys" -> DataProperty(NamedAddr("GLOBAL.Set.prototype.values"), T, F, T),
        "size" -> AccessorProperty(NamedAddr("GLOBAL.Set.prototype.size"), U, F, T)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.iterator") -> DataProperty(NamedAddr("GLOBAL.Set.prototype.values"), T, F, T),
          NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("Set"), F, F, T)
        )
    ),
    "GLOBAL.Set.prototype.size" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTgetSetDOTprototypeDOTsize.func
      ),
      nmap = NMap(
        "name" -> DataProperty(Str("get size"), F, F, T),
        "length" -> DataProperty(Num(0.0), F, F, T)
      )
    ),
    "GLOBAL.INTRINSIC_SetIteratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.INTRINSIC_IteratorPrototype")
      ),
      nmap = NMap() ++ Map(
        NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("Set Iterator"), F, F, T)
      )
    ),
    "GLOBAL.WeakMap" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTWeakMap.func
      ),
      nmap = NMap(
        "prototype" -> DataProperty(NamedAddr("GLOBAL.WeakMap.prototype"), F, F, F)
      )
    ),
    "GLOBAL.WeakMap.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.WeakMap"), T, F, T)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("WeakMap"), F, F, T)
        )
    ),
    "GLOBAL.WeakSet" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTWeakSet.func
      ),
      nmap = NMap(
        "prototype" -> DataProperty(NamedAddr("GLOBAL.WeakSet.prototype"), F, F, F)
      )
    ),
    "GLOBAL.WeakSet.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.WeakSet"), T, F, T)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("WeakSet"), F, F, T)
        )
    ),
    "GLOBAL.INTRINSIC_IteratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap()
    ),
    "GLOBAL.INTRINSIC_AsyncIteratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap()
    ),
    "GLOBAL.INTRINSIC_AsyncFromSyncIteratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.INTRINSIC_AsyncIteratorPrototype")
      ),
      nmap = NMap()
    ),
    "GLOBAL.INTRINSIC_GeneratorFunction" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Function"),
        "Code" -> GLOBALDOTGeneratorFunction.func,
        "Extensible" -> Bool(true),
        "ScriptOrModule" -> Null,
        "Realm" -> NamedAddr("REALM")
      ),
      nmap = NMap(
        "name" -> DataProperty(Str("GeneratorFunction"), T, F, T),
        "length" -> DataProperty(Num(1.0), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.INTRINSIC_Generator"), F, F, F)
      )
    ),
    "GLOBAL.INTRINSIC_Generator" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.INTRINSIC_GeneratorFunction"), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.INTRINSIC_GeneratorPrototype"), F, F, T)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("GeneratorFunction"), F, F, T)
        )
    ),
    "GLOBAL.INTRINSIC_GeneratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.INTRINSIC_IteratorPrototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.INTRINSIC_Generator"), F, F, T)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("Generator"), F, F, T)
        )
    ),
    "GLOBAL.INTRINSIC_AsyncGeneratorFunction" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Function"),
        "Code" -> GLOBALDOTAsyncGeneratorFunction.func,
        "Extensible" -> Bool(true),
        "ScriptOrModule" -> Null,
        "Realm" -> NamedAddr("REALM")
      ),
      nmap = NMap(
        "name" -> DataProperty(Str("AsyncGeneratorFunction"), T, F, T),
        "length" -> DataProperty(Num(1.0), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.INTRINSIC_AsyncGenerator"), F, F, F)
      )
    ),
    "GLOBAL.INTRINSIC_AsyncGenerator" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.INTRINSIC_AsyncGeneratorFunction"), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.INTRINSIC_AsyncGeneratorPrototype"), F, F, T)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("AsyncGeneratorFunction"), F, F, T)
        )
    ),
    "GLOBAL.INTRINSIC_AsyncGeneratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.INTRINSIC_AsyncIteratorPrototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.INTRINSIC_AsyncGenerator"), F, F, T)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("AsyncGenerator"), F, F, T)
        )
    ),
    "GLOBAL.Promise" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTPromise.func
      ),
      nmap = NMap(
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Promise.prototype"), F, F, F)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.species") -> AccessorProperty(NamedAddr("GLOBAL.Promise.SYMBOL_species"), U, F, T)
        )
    ),
    "GLOBAL.Promise.SYMBOL_species" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func("GLOBAL.Promise.SYMBOL_species", List(Id("this")), None, IReturn(ERef(RefId(Id("this")))))
      ),
      nmap = NMap(
        "name" -> DataProperty(Str("get [Symbol.species]"), F, F, T),
        "length" -> DataProperty(Num(0.0), F, F, T)
      )
    ),
    "GLOBAL.Promise.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Promise"), F, F, F)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("Promise"), F, F, T)
        )
    ),
    "GLOBAL.INTRINSIC_AsyncFunction" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Function"),
        "Code" -> GLOBALDOTAsyncFunction.func,
        "Extensible" -> Bool(true),
        "ScriptOrModule" -> Null,
        "Realm" -> NamedAddr("REALM")
      ),
      nmap = NMap(
        "name" -> DataProperty(Str("AsyncFunction"), F, F, T),
        "length" -> DataProperty(Num(1.0), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.INTRINSIC_AsyncFunctionPrototype"), F, F, F)
      )
    ),
    "GLOBAL.INTRINSIC_AsyncFunctionPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.INTRINSIC_AsyncFunction"), F, F, T)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.toStringTag") -> DataProperty(Str("AsyncFunction"), F, F, T)
        )
    )
  ) /: errList) {
      case (m, (errName, func)) => m ++ getErrMap(errName, func)
    }

  private val singletonInfo: List[String] = List(
    "asyncIterator",
    "hasInstance",
    "isConcatSpreadable",
    "iterator",
    "match",
    "replace",
    "search",
    "species",
    "split",
    "toPrimitive",
    "toStringTag",
    "unscopables"
  )

  private val notSupportedInfo: List[String] = List(
    "ArrayBuffer",
    "DataView",
    "Date",
    "Float32Array",
    "Float64Array",
    "Int8Array",
    "Int16Array",
    "Int32Array",
    "Proxy",
    "RegExp",
    "SharedArrayBuffer",
    "Uint8Array",
    "Uint8ClampedArray",
    "Uint16Array",
    "Uint32Array",
    "Atomics",
    "JSON",
    "Math",
    "Reflect",
    // test262
    // "print",
    "$262"
  )
}
