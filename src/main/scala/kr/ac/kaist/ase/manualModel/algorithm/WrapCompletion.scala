package kr.ac.kaist.ase.manualModel

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._
object WrapCompletion {
  val func: Func = Func("WrapCompletion", List(Id("val")), parseInst(
    s"""if (= (typeof val) "Completion") {
        return val
    } else {
        let temp = (new Completion())
        temp.Type = normal
        temp.Value = val
        temp.Target = empty
        return temp
    } """
  ))
}
