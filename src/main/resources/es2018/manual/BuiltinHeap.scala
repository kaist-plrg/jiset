package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core._

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
    case name => (NamedAddr(SYMBOL_PREFIX + name) -> CoreSymbol("Symbol." + name))
  }.toMap) ++ (notSupportedInfo.map {
    case name => (NamedAddr("GLOBAL." + name) -> CoreNotSupported(name))
  }.toMap)

  private def addDesc(
    name: String,
    m: Map[Addr, Obj],
    k: Value,
    prop: Property
  ): Map[Addr, Obj] = {
    val Property(v, w, e, c) = prop
    m + (
      NamedAddr(s"DESC:$name${getPropStr(k)}") ->
      CoreMap(Ty("PropertyDescriptor"), Map(
        Str("Value") -> v,
        Str("Writable") -> Bool(w),
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

  private val SYMBOL_PREFIX = "GLOBAL.Symbol."
  private def getPropStr(value: Value): String = value match {
    case Str(str) => s".$str"
    case _ => s"[${beautify(value)}]"
  }

  private case class Property(
    value: Value,
    writable: Boolean,
    enumerable: Boolean,
    configurable: Boolean
  )
  private val T = true
  private val F = false

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
        "Prototype" -> NamedAddr("GLOBAL.Error"),
        "Code" -> errFunc
      ),
      nmap = NMap(
        "name" -> Property(Str(errName), T, F, T),
        "prototype" -> Property(NamedAddr(s"GLOBAL.$errName.prototype"), F, F, F)
      )
    ),
    s"GLOBAL.$errName.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Error.prototype")
      ),
      nmap = NMap(
        "constructor" -> Property(NamedAddr(s"GLOBAL.$errName"), T, F, T),
        "message" -> Property(Str(""), T, F, T),
        "name" -> Property(Str(errName), T, F, T)
      )
    )
  )

  private val mapInfo: Map[String, Struct] = (Map(
    "GLOBAL" -> Struct(
      typeName = "Map",
      imap = IMap(),
      nmap = NMap(
        "Infinity" -> Property(Num(Double.PositiveInfinity), F, F, F),
        "NaN" -> Property(Num(Double.NaN), F, F, F),
        "undefined" -> Property(Undef, F, F, F),
        "Array" -> Property(NamedAddr("GLOBAL.Array"), T, F, T),
        "ArrayBuffer" -> Property(NamedAddr("GLOBAL.ArrayBuffer"), T, F, T),
        "Boolean" -> Property(NamedAddr("GLOBAL.Boolean"), T, F, T),
        "DataView" -> Property(NamedAddr("GLOBAL.DataView"), T, F, T),
        "Date" -> Property(NamedAddr("GLOBAL.Date"), T, F, T),
        "Error" -> Property(NamedAddr("GLOBAL.Error"), T, F, T),
        "EvalError" -> Property(NamedAddr("GLOBAL.EvalError"), T, F, T),
        "Float32Array" -> Property(NamedAddr("GLOBAL.Float32Array"), T, F, T),
        "Float64Array" -> Property(NamedAddr("GLOBAL.Float64Array"), T, F, T),
        "Function" -> Property(NamedAddr("GLOBAL.Function"), T, F, T),
        "Int8Array" -> Property(NamedAddr("GLOBAL.Int8Array"), T, F, T),
        "Int16Array" -> Property(NamedAddr("GLOBAL.Int16Array"), T, F, T),
        "Int32Array" -> Property(NamedAddr("GLOBAL.Int32Array"), T, F, T),
        "Map" -> Property(NamedAddr("GLOBAL.Map"), T, F, T),
        "Number" -> Property(NamedAddr("GLOBAL.Number"), T, F, T),
        "Object" -> Property(NamedAddr("GLOBAL.Object"), T, F, T),
        "Promise" -> Property(NamedAddr("GLOBAL.Promise"), T, F, T),
        "Proxy" -> Property(NamedAddr("GLOBAL.Proxy"), T, F, T),
        "RangeError" -> Property(NamedAddr("GLOBAL.RangeError"), T, F, T),
        "ReferenceError" -> Property(NamedAddr("GLOBAL.ReferenceError"), T, F, T),
        "RegExp" -> Property(NamedAddr("GLOBAL.RegExp"), T, F, T),
        "Set" -> Property(NamedAddr("GLOBAL.Set"), T, F, T),
        "SharedArrayBuffer" -> Property(NamedAddr("GLOBAL.SharedArrayBuffer"), T, F, T),
        "String" -> Property(NamedAddr("GLOBAL.String"), T, F, T),
        "Symbol" -> Property(NamedAddr("GLOBAL.Symbol"), T, F, T),
        "SyntaxError" -> Property(NamedAddr("GLOBAL.SyntaxError"), T, F, T),
        "TypeError" -> Property(NamedAddr("GLOBAL.TypeError"), T, F, T),
        "Uint8Array" -> Property(NamedAddr("GLOBAL.Uint8Array"), T, F, T),
        "Uint8ClampedArray" -> Property(NamedAddr("GLOBAL.Uint8ClampedArray"), T, F, T),
        "Uint16Array" -> Property(NamedAddr("GLOBAL.Uint16Array"), T, F, T),
        "Uint32Array" -> Property(NamedAddr("GLOBAL.Uint32Array"), T, F, T),
        "URIError" -> Property(NamedAddr("GLOBAL.URIError"), T, F, T),
        "WeakMap" -> Property(NamedAddr("GLOBAL.WeakMap"), T, F, T),
        "WeakSet" -> Property(NamedAddr("GLOBAL.WeakSet"), T, F, T),
        "Atomics" -> Property(NamedAddr("GLOBAL.Atomics"), T, F, T),
        "JSON" -> Property(NamedAddr("GLOBAL.JSON"), T, F, T),
        "Math" -> Property(NamedAddr("GLOBAL.Math"), T, F, T),
        "Reflect" -> Property(NamedAddr("GLOBAL.Reflect"), T, F, T)
      )
    ),
    "GLOBAL.Object" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTObject.func
      ),
      nmap = NMap(
        "length" -> Property(Num(1.0), F, F, T),
        "prototype" -> Property(NamedAddr("GLOBAL.Object.prototype"), F, F, F)
      )
    ),
    "GLOBAL.Object.prototype" -> Struct(
      typeName = "ImmutablePrototypeExoticObject",
      imap = IMap(
        "Prototype" -> Null
      ),
      nmap = NMap(
        "constructor" -> Property(NamedAddr("GLOBAL.Object"), T, F, T)
      )
    ),
    "GLOBAL.Function" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTFunction.func
      ),
      nmap = NMap(
        "length" -> Property(Num(1.0), F, F, T),
        "prototype" -> Property(NamedAddr("GLOBAL.Function.prototype"), F, F, F)
      )
    ),
    "GLOBAL.Function.prototype" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "length" -> Property(Num(0.0), F, F, T),
        "name" -> Property(Str(""), F, F, T),
        "constructor" -> Property(NamedAddr("GLOBAL.Function"), T, F, T)
      )
    ),
    "GLOBAL.Boolean" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTBoolean.func
      ),
      nmap = NMap(
        "length" -> Property(Num(1.0), F, F, T),
        "prototype" -> Property(NamedAddr("GLOBAL.Boolean.prototype"), F, F, F)
      )
    ),
    "GLOBAL.Boolean.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "BooleanData" -> Bool(false),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "constructor" -> Property(NamedAddr("GLOBAL.Boolean"), T, F, T)
      )
    ),
    "GLOBAL.Symbol" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTSymbol.func
      ),
      nmap = NMap(
        "asyncIterator" -> Property(NamedAddr("GLOBAL.Symbol.asyncIterator"), F, F, F),
        "hasInstance" -> Property(NamedAddr("GLOBAL.Symbol.hasInstance"), F, F, F),
        "isConcatSpreadable" -> Property(NamedAddr("GLOBAL.Symbol.isConcatSpreadable"), F, F, F),
        "iterator" -> Property(NamedAddr("GLOBAL.Symbol.iterator"), F, F, F),
        "match" -> Property(NamedAddr("GLOBAL.Symbol.match"), F, F, F),
        "replace" -> Property(NamedAddr("GLOBAL.Symbol.replace"), F, F, F),
        "search" -> Property(NamedAddr("GLOBAL.Symbol.search"), F, F, F),
        "species" -> Property(NamedAddr("GLOBAL.Symbol.species"), F, F, F),
        "split" -> Property(NamedAddr("GLOBAL.Symbol.split"), F, F, F),
        "toPrimitive" -> Property(NamedAddr("GLOBAL.Symbol.toPrimitive"), F, F, F),
        "toStringTag" -> Property(NamedAddr("GLOBAL.Symbol.toStringTag"), F, F, F),
        "unscopables" -> Property(NamedAddr("GLOBAL.Symbol.unscopables"), F, F, F),
        "length" -> Property(Num(0.0), F, F, T),
        "prototype" -> Property(NamedAddr("GLOBAL.Symbol.prototype"), F, F, F)
      )
    ),
    "GLOBAL.Symbol.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "constructor" -> Property(NamedAddr("GLOBAL.Symbol"), T, F, T)
      ) ++ Map(
          NamedAddr("GLOBAL.Symbol.toStringTag") -> Property(Str("Symbol"), F, F, T)
        )
    ),
    "GLOBAL.Error" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTError.func
      ),
      nmap = NMap(
        "prototype" -> Property(NamedAddr("GLOBAL.Error.prototype"), F, F, F)
      )
    ),
    "GLOBAL.Error.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "constructor" -> Property(NamedAddr("GLOBAL.Error"), T, F, T),
        "message" -> Property(Str(""), T, F, T),
        "name" -> Property(Str("Error"), T, F, T)
      )
    ),
    "GLOBAL.Number" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> GLOBALDOTNumber.func
      ),
      nmap = NMap(
        "EPSILON" -> Property(Num(math.ulp(1.0)), F, F, F),
        "MAX_SAFE_INTEGER" -> Property(INum(9007199254740991L), F, F, F),
        "MAX_VALUE" -> Property(Num(Double.MaxValue), F, F, F),
        "MIN_SAFE_INTEGER" -> Property(INum(-9007199254740991L), F, F, F),
        "MIN_VALUE" -> Property(Num(Double.MinValue), F, F, F),
        "NaN" -> Property(Num(Double.NaN), F, F, F),
        "NEGATIVE_INFINITY" -> Property(Num(Double.NegativeInfinity), F, F, F),
        "parseFloat" -> Property(NamedAddr("GLOBAL.parseFloat"), F, F, F),
        "parseInt" -> Property(NamedAddr("GLOBAL.parseInt"), F, F, F),
        "POSITIVE_INFINITY" -> Property(Num(Double.PositiveInfinity), F, F, F),
        "prototype" -> Property(NamedAddr("GLOBAL.Number.prototype"), F, F, F)
      )
    ),
    "GLOBAL.Number.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "NumberData" -> Num(0.0),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "constructor" -> Property(NamedAddr("GLOBAL.Number"), T, F, T)
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
    "Math",
    "Date",
    "RegExp",
    "JSON"
  )
}
