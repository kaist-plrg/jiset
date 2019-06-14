package kr.ac.kaist.ase.manualModel

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._
object ToNumber {
  val func: Func = Func("ToNumber", List(Id("argument")), parseInst(
    s"""if (= (typeof argument) "Number") {
          return argument
        } else {
          return undefined
        }"""
  ))
}
