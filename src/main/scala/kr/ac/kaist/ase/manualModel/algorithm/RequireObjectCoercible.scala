package kr.ac.kaist.ase.manualModel

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._
object RequireObjectCoercible {
  val func: Func = Func("RequireObjectCoercible", List(Id("argument")), None, parseInst(
    s"""if (|| (= (typeof argument) "Undefined") (= (typeof argument) "Null")) {
      return (new Completion("Type" -> throw, "Value" -> "TypeError", "Target" -> empty))
    } else {
      return argument
    }"""
  ))
}
