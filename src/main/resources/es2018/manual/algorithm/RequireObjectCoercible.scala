package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._
object RequireObjectCoercible {
  val func: Func = Func("RequireObjectCoercible", List(Id("argument")), None, parseInst(
    s"""if (|| (= (typeof argument) "Undefined") (= (typeof argument) "Null")) {
      return (new Completion("Type" -> CONST_throw, "Value" -> "TypeError", "Target" -> CONST_empty))
    } else {
      return argument
    }"""
  ))
}
