package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._
object ToString {
  val func: Func = Func("ToString", List(Id("argument")), None, parseInst(
    s"""{
      app atype = (Type argument)
      if (= atype "Undefined") return "undefined"
      else if (= atype "Null") return "null"
      else if (= atype "Boolean") if argument return "true" else return "false"
      else if (= atype "Number") {
        app __x0__ = (NumberToString argument)
        return __x0__
      }
      else if (= atype "String") return argument
      else if (= atype "Symbol")  return (new Completion (
        "Type" -> CONST_throw,
        "Value" -> (new OrdinaryObject(
          "Prototype" -> INTRINSIC_TypeErrorPrototype,
          "ErrorData" -> undefined,
          "SubMap" -> (new SubMap())
        )),
        "Target" -> CONST_empty
      ))
      else {
        app __x0__ = (ToPrimitive argument "String")
        if (= (typeof __x0__) "Completion") {
          if (= __x0__.Type CONST_normal) __x0__ = __x0__.Value
          else return __x0__
        } else {}
        let primValue = __x0__
        app __x1__ = (ToString primValue)
        if (= (typeof __x1__) "Completion") {
          if (= __x1__.Type CONST_normal) __x1__ = __x1__.Value
          else return __x1__
        } else {}
        return __x1__
      }
    }"""
  ))
}
