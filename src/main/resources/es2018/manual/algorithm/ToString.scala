package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._
object ToString {
  val func: Func = Func("ToString", List(Id("argument")), None, parseInst(
    s"""{
      let atype = (typeof argument)
      if (= atype "Undefined") {
        return "undefined"
      } else if (= atype "Null") {
        return "null"
      } else if (= atype "Boolean") {
        if argument return "true"
        else return "false"
      } else if (= atype "Number") {
        return (NumberToString argument)
      } else if (= atype "String") {
        return argument
      } else if (= atype "Symbol") {
        return (ThrowCompletion (new TypeError()))
      } else if (= atype "Object") {
        return (ToString (ToPrimitive argument "string"))
      } else {
        return undefined
      }
    }"""
  ))
}
