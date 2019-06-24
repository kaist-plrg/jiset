package kr.ac.kaist.ase.manualModel

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._
object ToBoolean {
  val func: Func = Func("ToBoolean", List(Id("argument")), None, parseInst(
    s"""if (= (typeof argument) "Boolean") {
          return argument
        } else if (= (typeof argument) "Number") {
          if (|| (= argument 0.0) (= argument NaN)) return false
          else return true
        } else {
          return undefined
        }"""
  ))
}
