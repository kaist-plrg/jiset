object ToObject {
  val func: Func = Func("ToObject", List(Id("argument")), None, parseInst(
    s"""{
          app atype = (Type argument)
          if (= atype "Undefined") {
            return (new Completion (
              "Type" -> CONST_throw,
              "Value" -> (new OrdinaryObject(
                "Prototype" -> INTRINSIC_TypeErrorPrototype,
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
                "ErrorData" -> undefined,
                "SubMap" -> (new SubMap())
              )),
              "Target" -> CONST_empty
            ))
          } else if (= atype "Boolean") {
            let obj = (new OrdinaryObject("Prototype" -> INTRINSIC_BooleanPrototype))
            obj.BooleanData = argument
            obj.SubMap = (new SubMap())
            return obj
          } else if (= atype "Number") {
            let obj = (new OrdinaryObject("Prototype" -> INTRINSIC_NumberPrototype))
            obj.NumberData = argument
            obj.SubMap = (new SubMap())
            return obj
          } else if (= atype "String") {
            let obj = (new StringExoticObject("Prototype" -> INTRINSIC_StringPrototype))
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
            let obj = (new OrdinaryObject("Prototype" -> INTRINSIC_SymbolPrototype))
            obj.SymbolData = argument
            obj.SubMap = (new SubMap())
            return obj
          } else {
            return argument
          }
        }"""
  ))
}
