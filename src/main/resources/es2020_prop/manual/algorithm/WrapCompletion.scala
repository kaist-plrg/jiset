package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._
object WrapCompletion {
  val func: Func = Func("WrapCompletion", List(Id("val")), None, parseInst(
    s"""if (= (typeof val) "Completion") {
        return val
    } else {
      app completion = (NormalCompletion val)
      return completion
    } """
  ))
}