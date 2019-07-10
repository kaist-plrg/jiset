package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._
object ToObject {
  val func: Func = Func("ToObject", List(Id("argument")), None, parseInst(
    s"""{
          let atype = (typeof argument)
          if (= atype "Undefined") {
            return (new Completion ( "Type" -> CONST_throw, "Value" -> (new TypeError ()), "Target" -> CONST_empty ))
          } else if (= atype "Null") {
            return (new Completion ( "Type" -> CONST_throw, "Value" -> (new TypeError ()), "Target" -> CONST_empty ))
          } else if (= atype "Boolean") {
            let obj = (new OrdinaryObject("Prototype" -> INTRINSIC_BooleanPrototype))
            obj.BooleanData = argument
            obj.SubMap = (new SubMap())
            return obj
          } else if (= atype "Number") {
            return (new Completion ( "Type" -> CONST_throw, "Value" -> (new TypeError ()), "Target" -> CONST_empty ))
          } else if (= atype "String") {
            return (new Completion ( "Type" -> CONST_throw, "Value" -> (new TypeError ()), "Target" -> CONST_empty ))
          } else if (= atype "Symbol") {
            let obj = (new OrdinaryObject("Prototype" -> INTRINSIC_SymbolPrototype))
            obj.SymbolData = argument
            obj.SubMap = (new SubMap())
            return obj
          } else if (= atype "Object") {
            return argument
          } else {
            return argumeht
          }
        }"""
  ))
}
