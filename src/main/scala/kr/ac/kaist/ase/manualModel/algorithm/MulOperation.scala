package kr.ac.kaist.ase.manualModel

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._
object MulOperation {
  val func: Func = Func("MulOperation", List(Id("op"), Id("lnum"), Id("rnum")), None, parseInst(
    s"""if (= op "*") {
          return ( * lnum rnum )
        } else if (= op "/") {
          return ( / lnum rnum )
        } else if (= op "%") {
          return ( % lnum rnum )
        } else {
          return undefined
        }"""
  ))
}
