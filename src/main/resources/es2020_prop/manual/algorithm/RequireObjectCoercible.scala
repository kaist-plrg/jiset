package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._
object RequireObjectCoercible {
  val func: Func = Func("RequireObjectCoercible", List(Id("argument")), None, parseInst(
    s"""if (|| (= (typeof argument) "Undefined") (= (typeof argument) "Null")) {
      return (new Completion (
        "Type" -> CONST_throw,
        "Value" -> (new OrdinaryObject(
          "Prototype" -> INTRINSIC_TypeErrorPrototype,
          "ErrorData" -> undefined,
          "SubMap" -> (new SubMap())
        )),
        "Target" -> CONST_empty
      ))
    } else {
      return argument
    }"""
  ))
}
