object ToObject extends Algorithm {
  val name: String = "ToObject"
  val length: Int = 1
  val lang: Boolean = true
  val func: Func = FixUIdWalker(parseFunc(""""ToObject" (argument) => {
    app atype = (Type argument)
    if (= atype "Undefined") {
      return (new Completion (
        "Type" -> CONST_throw,
        "Value" -> (new OrdinaryObject(
          "Prototype" -> INTRINSIC_TypeErrorPrototype,
          "Extensible" -> false,
          "ErrorData" -> undefined,
          "SubMap" -> (new SubMap())
        )),
        "Target" -> CONST_empty
      ))
    } else if (= atype "Null") {
      return (new Completion (
        "Type" -> CONST_throw,
        "Value" -> (new OrdinaryObject(
          "Prototype" -> INTRINSIC_TypeErrorPrototype,
          "Extensible" -> false,
          "ErrorData" -> undefined,
          "SubMap" -> (new SubMap())
        )),
        "Target" -> CONST_empty
      ))
    } else if (= atype "Boolean") {
      let obj = (new OrdinaryObject("Prototype" -> INTRINSIC_BooleanPrototype, "Extensible" -> true))
      obj.BooleanData = argument
      obj.SubMap = (new SubMap())
      return obj
    } else if (= atype "Number") {
      let obj = (new OrdinaryObject("Prototype" -> INTRINSIC_NumberPrototype, "Extensible" -> true))
      obj.NumberData = argument
      obj.SubMap = (new SubMap())
      return obj
    } else if (= atype "String") {
      let obj = (new StringExoticObject("Prototype" -> INTRINSIC_StringPrototype, "Extensible" -> true))
      obj.StringData = argument
      if (= obj["HasProperty"] absent) obj["HasProperty"] = OrdinaryObjectDOTHasProperty else {}
      if (= obj["DefineOwnProperty"] absent) obj["DefineOwnProperty"] = OrdinaryObjectDOTDefineOwnProperty else {}
      if (= obj["Set"] absent) obj["Set"] = OrdinaryObjectDOTSet else {}
      if (= obj["SetPrototypeOf"] absent) obj["SetPrototypeOf"] = OrdinaryObjectDOTSetPrototypeOf else {}
      if (= obj["Get"] absent) obj["Get"] = OrdinaryObjectDOTGet else {}
      if (= obj["PreventExtensions"] absent) obj["PreventExtensions"] = OrdinaryObjectDOTPreventExtensions else {}
      if (= obj["Delete"] absent) obj["Delete"] = OrdinaryObjectDOTDelete else {}
      if (= obj["GetOwnProperty"] absent) obj["GetOwnProperty"] = OrdinaryObjectDOTGetOwnProperty else {}
      if (= obj["OwnPropertyKeys"] absent) obj["OwnPropertyKeys"] = OrdinaryObjectDOTOwnPropertyKeys else {}
      if (= obj["GetPrototypeOf"] absent) obj["GetPrototypeOf"] = OrdinaryObjectDOTGetPrototypeOf else {}
      if (= obj["IsExtensible"] absent) obj["IsExtensible"] = OrdinaryObjectDOTIsExtensible else {}
      obj.SubMap = (new SubMap( "length" -> (new DataProperty( "Value" -> argument["length"], "Writable" -> false, "Enumerable" -> false, "Configurable" -> false))))
      return obj
    } else if (= atype "Symbol") {
      let obj = (new OrdinaryObject("Prototype" -> INTRINSIC_SymbolPrototype, "Extensible" -> true))
      obj.SymbolData = argument
      obj.SubMap = (new SubMap())
      return obj
    } else {
      return argument
    }
  }"""), this)
}
