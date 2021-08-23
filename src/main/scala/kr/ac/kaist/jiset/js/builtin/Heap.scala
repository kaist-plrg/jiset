package kr.ac.kaist.jiset.js.builtin

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js._

object Heap {
  // shortcuts
  private val T = true
  private val F = false
  private val U = Undef

  // get map for heap
  lazy val map: Map[Addr, Obj] = {
    val builtinMap = for {
      (name, struct) <- builtins
      pair <- struct.toMap(name)
    } yield pair
    val notSupportedMap = for {
      givenName <- notSupportedNames
      name = globalName(givenName)
      addr = NamedAddr(name)
      obj = IRNotSupported("OrdinaryObject", name)
    } yield addr -> obj
    builtinMap ++ notSupportedMap
  }

  private def builtins: Map[String, Struct] = errors ++ Map(
    "REALM" -> Struct(
      typeName = "RealmRecord",
      imap = IMap(),
      nmap = NMap(),
    ),
    "GLOBAL" -> Struct(
      typeName = "Map",
      imap = IMap(),
      nmap = NMap(
        "print" -> DataProperty(NamedAddr("GLOBAL.print"), T, F, T),
        "$262" -> DataProperty(NamedAddr("GLOBAL.$262"), T, F, T),
        "globalThis" -> DataProperty(NamedAddr("GLOBAL"), T, F, T),
        "Infinity" -> DataProperty(Num(Double.PositiveInfinity), F, F, F),
        "NaN" -> DataProperty(Num(Double.NaN), F, F, F),
        "undefined" -> DataProperty(Undef, F, F, F),
        "Array" -> DataProperty(NamedAddr("GLOBAL.Array"), T, F, T),
        "ArrayBuffer" -> DataProperty(NamedAddr("GLOBAL.ArrayBuffer"), T, F, T),
        "BigInt64Array" -> DataProperty(NamedAddr("GLOBAL.BigInt64Array"), T, F, T),
        "BigUint64Array" -> DataProperty(NamedAddr("GLOBAL.BigUint64Array"), T, F, T),
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
        "BigInt" -> DataProperty(NamedAddr("GLOBAL.BigInt"), T, F, T),
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
        "Code" -> Func(algoMap("GLOBAL.HostPrint")),
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(0.0), F, F, T),
      )
    ),
    "GLOBAL.Object" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Object")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T),
        "name" -> DataProperty(Str("Object"), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Object.prototype"), F, F, F),
      )
    ),
    "GLOBAL.Object.prototype" -> Struct(
      typeName = "ImmutablePrototypeExoticObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> Null,
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
        "Code" -> Func(algoMap("GLOBAL.Function")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T),
        "name" -> DataProperty(Str("Function"), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Function.prototype"), F, F, F)
      )
    ),
    "GLOBAL.Function.prototype" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Function.prototype")),
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(0.0), F, F, T),
        "name" -> DataProperty(Str(""), F, F, T),
        "constructor" -> DataProperty(NamedAddr("GLOBAL.Function"), T, F, T),
        "caller" -> AccessorProperty(NamedAddr("GLOBAL.ThrowTypeError"), NamedAddr("GLOBAL.ThrowTypeError"), F, T),
        "arguments" -> AccessorProperty(NamedAddr("GLOBAL.ThrowTypeError"), NamedAddr("GLOBAL.ThrowTypeError"), F, T),
        ("@hasInstance") -> DataProperty(NamedAddr("GLOBAL.Function.prototype[SYMBOL_hasInstance]"), F, F, F),
      )
    ),
    "GLOBAL.Function.prototype[SYMBOL_hasInstance]" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Function.prototype[SYMBOL_hasInstance]")),
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T),
        "name" -> DataProperty(Str("[Symbol.hasInstance]"), F, F, T)
      )
    ),
    "GLOBAL.ThrowTypeError" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(false),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.ThrowTypeError")),
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(0.0), F, F, F),
        "name" -> DataProperty(Str("ThrowTypeError"), F, F, F),
      )
    ),
    "GLOBAL.Boolean" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Boolean")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
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
        "Code" -> Func(algoMap("GLOBAL.Symbol")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
      ),
      nmap = NMap(
        "asyncIterator" -> DataProperty(NamedAddr("GLOBAL.Symbol.asyncIterator"), F, F, F),
        "hasInstance" -> DataProperty(NamedAddr("GLOBAL.Symbol.hasInstance"), F, F, F),
        "isConcatSpreadable" -> DataProperty(NamedAddr("GLOBAL.Symbol.isConcatSpreadable"), F, F, F),
        "iterator" -> DataProperty(NamedAddr("GLOBAL.Symbol.iterator"), F, F, F),
        "match" -> DataProperty(NamedAddr("GLOBAL.Symbol.match"), F, F, F),
        "matchAll" -> DataProperty(NamedAddr("GLOBAL.Symbol.matchAll"), F, F, F),
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
        "constructor" -> DataProperty(NamedAddr("GLOBAL.Symbol"), T, F, T),
        "description" -> AccessorProperty(NamedAddr("GLOBAL.Symbol.prototype.description"), U, F, T),
        "@toStringTag" -> DataProperty(Str("Symbol"), F, F, T),
        "@toPrimitive" -> DataProperty(NamedAddr("GLOBAL.Symbol.prototype[SYMBOL_toPrimitive]"), F, F, T),
      )
    ),
    "GLOBAL.Symbol.prototype[SYMBOL_toPrimitive]" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Symbol.prototype[SYMBOL_toPrimitive]")),
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T),
        "name" -> DataProperty(Str("[Symbol.toPrimitive]"), F, F, T)
      )
    ),
    "GLOBAL.Symbol.prototype.description" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Symbol.prototype.description")),
      ),
      nmap = NMap(
        "name" -> DataProperty(Str("get description"), F, F, T),
        "length" -> DataProperty(Num(0.0), F, F, T)
      )
    ),
    "GLOBAL.Error" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Error")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
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
        "Code" -> Func(algoMap("GLOBAL.Number")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
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
    "GLOBAL.BigInt" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.BigInt")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
      ),
      nmap = NMap(
        "prototype" -> DataProperty(NamedAddr("GLOBAL.BigInt.prototype"), F, F, F),
        "asIntN" -> DataProperty(NamedAddr("GLOBAL.BigInt.asIntN"), T, F, T),
        "asUintN" -> DataProperty(NamedAddr("GLOBAL.BigInt.asUintN"), T, F, T)
      )
    ),
    "GLOBAL.BigInt.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.BigInt"), T, F, T),
        "@toStringTag" -> DataProperty(Str("BigInt"), F, F, T),
      )
    ),
    "GLOBAL.String" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.String")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
      ),
      nmap = NMap(
        "prototype" -> DataProperty(NamedAddr("GLOBAL.String.prototype"), F, F, F)
      )
    ),
    "GLOBAL.String.prototype" -> Struct(
      typeName = "StringExoticObject",
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
    "GLOBAL.StringIteratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.IteratorPrototype")
      ),
      nmap = NMap(
        "@toStringTag" -> DataProperty(Str("String Iterator"), F, F, T),
      ),
    ),
    "GLOBAL.Array" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Array")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Array.prototype"), F, F, F),
        "@species" -> AccessorProperty(NamedAddr("GLOBAL.Array[SYMBOL_species]"), U, F, T)
      )
    ),
    "GLOBAL.Array[SYMBOL_species]" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Array[SYMBOL_species]")),
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
        "constructor" -> DataProperty(NamedAddr("GLOBAL.Array"), T, F, T),
        "@iterator" -> DataProperty(NamedAddr("GLOBAL.Array.prototype.values"), T, F, T),
        "@unscopables" -> DataProperty(NamedAddr("GLOBAL.Array.prototype[SYMBOL_unscopables]"), F, F, T),
      )
    ),
    "GLOBAL.Array.prototype[SYMBOL_unscopables]" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> Null
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
    "GLOBAL.ArrayIteratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.IteratorPrototype")
      ),
      nmap = NMap(
        "@toStringTag" -> DataProperty(Str("Array Iterator"), F, F, T),
      )
    ),
    "GLOBAL.Map" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Map")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
      ),
      nmap = NMap(
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Map.prototype"), F, F, F),
        "@species" -> AccessorProperty(NamedAddr("GLOBAL.Map[SYMBOL_species]"), U, F, T),
      )
    ),
    "GLOBAL.Map[SYMBOL_species]" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Map[SYMBOL_species]")),
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
        "size" -> AccessorProperty(NamedAddr("GLOBAL.Map.prototype.size"), U, F, T),
        "@iterator" -> DataProperty(NamedAddr("GLOBAL.Map.prototype.entries"), T, F, T),
        "@toStringTag" -> DataProperty(Str("Map"), F, F, T),
      )
    ),
    "GLOBAL.Map.prototype.size" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Map.prototype.size")),
      ),
      nmap = NMap(
        "name" -> DataProperty(Str("get size"), F, F, T),
        "length" -> DataProperty(Num(0.0), F, F, T)
      )
    ),
    "GLOBAL.MapIteratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.IteratorPrototype")
      ),
      nmap = NMap(
        "@toStringTag" -> DataProperty(Str("Map Iterator"), F, F, T),
      )
    ),
    "GLOBAL.Set" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Set")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
      ),
      nmap = NMap(
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Set.prototype"), F, F, F),
        "@species" -> AccessorProperty(NamedAddr("GLOBAL.Set[SYMBOL_species]"), U, F, T),
      )
    ),
    "GLOBAL.Set[SYMBOL_species]" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Set[SYMBOL_species]")),
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
        "size" -> AccessorProperty(NamedAddr("GLOBAL.Set.prototype.size"), U, F, T),
        "@iterator" -> DataProperty(NamedAddr("GLOBAL.Set.prototype.values"), T, F, T),
        "@toStringTag" -> DataProperty(Str("Set"), F, F, T),
      )
    ),
    "GLOBAL.Set.prototype.size" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Set.prototype.size")),
      ),
      nmap = NMap(
        "name" -> DataProperty(Str("get size"), F, F, T),
        "length" -> DataProperty(Num(0.0), F, F, T)
      )
    ),
    "GLOBAL.SetIteratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.IteratorPrototype")
      ),
      nmap = NMap(
        "@toStringTag" -> DataProperty(Str("Set Iterator"), F, F, T),
      )
    ),
    "GLOBAL.WeakMap" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.WeakMap")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
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
        "constructor" -> DataProperty(NamedAddr("GLOBAL.WeakMap"), T, F, T),
        "@toStringTag" -> DataProperty(Str("WeakMap"), F, F, T),
      )
    ),
    "GLOBAL.WeakSet" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.WeakSet")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
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
        "constructor" -> DataProperty(NamedAddr("GLOBAL.WeakSet"), T, F, T),
        "@toStringTag" -> DataProperty(Str("WeakSet"), F, F, T),
      )
    ),
    "GLOBAL.IteratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap()
    ),
    "GLOBAL.AsyncIteratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Object.prototype")
      ),
      nmap = NMap()
    ),
    "GLOBAL.AsyncFromSyncIteratorPrototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.AsyncIteratorPrototype")
      ),
      nmap = NMap()
    ),
    "GLOBAL.GeneratorFunction" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Function"),
        "Code" -> Func(algoMap("GLOBAL.GeneratorFunction")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
        "Extensible" -> Bool(true),
        "ScriptOrModule" -> Null,
        "Realm" -> NamedAddr("REALM")
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T),
        "name" -> DataProperty(Str("GeneratorFunction"), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.GeneratorFunction.prototype"), F, F, F)
      )
    ),
    "GLOBAL.GeneratorFunction.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.GeneratorFunction"), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Generator.prototype"), F, F, T),
        "@toStringTag" -> DataProperty(Str("GeneratorFunction"), F, F, T),
      )
    ),
    "GLOBAL.Generator.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.IteratorPrototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.GeneratorFunction.prototype"), F, F, T),
        "@toStringTag" -> DataProperty(Str("Generator"), F, F, T),
      )
    ),
    "GLOBAL.AsyncGeneratorFunction" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Function"),
        "Code" -> Func(algoMap("GLOBAL.AsyncGeneratorFunction")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
        "Extensible" -> Bool(true),
        "ScriptOrModule" -> Null,
        "Realm" -> NamedAddr("REALM")
      ),
      nmap = NMap(
        "name" -> DataProperty(Str("AsyncGeneratorFunction"), F, F, T),
        "length" -> DataProperty(Num(1.0), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.AsyncGeneratorFunction.prototype"), F, F, F)
      )
    ),
    "GLOBAL.AsyncGeneratorFunction.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.AsyncGeneratorFunction"), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.AsyncGenerator.prototype"), F, F, T),
        "@toStringTag" -> DataProperty(Str("AsyncGeneratorFunction"), F, F, T),
      )
    ),
    "GLOBAL.AsyncGenerator.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.AsyncIteratorPrototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.AsyncGeneratorFunction.prototype"), F, F, T),
        "@toStringTag" -> DataProperty(Str("AsyncGenerator"), F, F, T),
      )
    ),
    "GLOBAL.Promise" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Promise")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
      ),
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T),
        "name" -> DataProperty(Str("Promise"), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.Promise.prototype"), F, F, F),
        "@species" -> AccessorProperty(NamedAddr("GLOBAL.Promise[SYMBOL_species]"), U, F, T),
      )
    ),
    "GLOBAL.Promise[SYMBOL_species]" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype"),
        "Code" -> Func(algoMap("GLOBAL.Promise[SYMBOL_species]")),
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
        "constructor" -> DataProperty(NamedAddr("GLOBAL.Promise"), T, F, T),
        "@toStringTag" -> DataProperty(Str("Promise"), F, F, T),
      )
    ),
    "GLOBAL.AsyncFunction" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Prototype" -> NamedAddr("GLOBAL.Function"),
        "Code" -> Func(algoMap("GLOBAL.AsyncFunction")),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
        "Extensible" -> Bool(true),
        "ScriptOrModule" -> Null,
        "Realm" -> NamedAddr("REALM")
      ),
      nmap = NMap(
        "name" -> DataProperty(Str("AsyncFunction"), F, F, T),
        "length" -> DataProperty(Num(1.0), F, F, T),
        "prototype" -> DataProperty(NamedAddr("GLOBAL.AsyncFunction.prototype"), F, F, F)
      )
    ),
    "GLOBAL.AsyncFunction.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Function.prototype")
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.AsyncFunction"), F, F, T),
        "@toStringTag" -> DataProperty(Str("AsyncFunction"), F, F, T),
      )
    ),
    "GLOBAL.Object.assign" -> Struct(
      typeName = "BuiltinFunctionObject",
      nmap = NMap(
        "length" -> DataProperty(Num(2.0), F, F, T)
      )
    ),
    "GLOBAL.Number.prototype.toString" -> Struct(
      typeName = "BuiltinFunctionObject",
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T)
      )
    ),
    "GLOBAL.String.fromCharCode" -> Struct(
      typeName = "BuiltinFunctionObject",
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T)
      )
    ),
    "GLOBAL.String.fromCodePoint" -> Struct(
      typeName = "BuiltinFunctionObject",
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T)
      )
    ),
    "GLOBAL.String.prototype.concat" -> Struct(
      typeName = "BuiltinFunctionObject",
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T)
      )
    ),
    "GLOBAL.Array.prototype.concat" -> Struct(
      typeName = "BuiltinFunctionObject",
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T)
      )
    ),
    "GLOBAL.Array.prototype.push" -> Struct(
      typeName = "BuiltinFunctionObject",
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T)
      )
    ),
    "GLOBAL.Array.prototype.unshift" -> Struct(
      typeName = "BuiltinFunctionObject",
      nmap = NMap(
        "length" -> DataProperty(Num(1.0), F, F, T)
      )
    ),
    "GLOBAL.Object.assign" -> Struct(
      typeName = "BuiltinFunctionObject",
      nmap = NMap(
        "length" -> DataProperty(Num(2.0), F, F, T)
      )
    ),
    "GLOBAL.AggregateError" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Error"),
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
      ),
      nmap = NMap(
        "prototype" -> DataProperty(NamedAddr("GLOBAL.AggregateError.prototype"), F, F, F),
      ),
    ),
    "GLOBAL.AggregateError.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Error.prototype"),
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr("GLOBAL.AggregateError"), T, F, T),
        "message" -> DataProperty(Str(""), T, F, T),
        "name" -> DataProperty(Str("AggregateError"), T, F, T),
      ),
    ),
  )

  private def errors: Map[String, Struct] = (for {
    (name, algo) <- errList
    (name, struct) <- getErrMap(name, Func(algo))
  } yield name -> struct).toMap

  private def errList: List[(String, Algo)] = List(
    ("EvalError", algoMap("GLOBAL.EvalError")),
    ("RangeError", algoMap("GLOBAL.RangeError")),
    ("ReferenceError", algoMap("GLOBAL.ReferenceError")),
    ("SyntaxError", algoMap("GLOBAL.SyntaxError")),
    ("TypeError", algoMap("GLOBAL.TypeError")),
    ("URIError", algoMap("GLOBAL.URIError")),
  )

  private def getErrMap(errName: String, errFunc: Func): Map[String, Struct] = Map(
    s"GLOBAL.$errName" -> Struct(
      typeName = "BuiltinFunctionObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Error"),
        "Code" -> errFunc,
        "Construct" -> Func(algoMap("BuiltinFunctionObject.Construct")),
      ),
      nmap = NMap(
        "name" -> DataProperty(Str(errName), F, F, T),
        "prototype" -> DataProperty(NamedAddr(s"GLOBAL.$errName.prototype"), F, F, F),
      ),
    ),
    s"GLOBAL.$errName.prototype" -> Struct(
      typeName = "OrdinaryObject",
      imap = IMap(
        "Extensible" -> Bool(true),
        "Prototype" -> NamedAddr("GLOBAL.Error.prototype"),
      ),
      nmap = NMap(
        "constructor" -> DataProperty(NamedAddr(s"GLOBAL.$errName"), T, F, T),
        "message" -> DataProperty(Str(""), T, F, T),
        "name" -> DataProperty(Str(errName), T, F, T),
      ),
    )
  )

  private val notSupportedNames: List[String] = List(
    "ArrayBuffer",
    "BigInt64Array",
    "BigUint64Array",
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
    "WeakRef",
    // others
    "Atomics",
    "JSON",
    // "Math",
    "Reflect",
    // test262
    "$262",
  )
}
