package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object abs {
  val func: Func = parseFunc(""""abs" (a) => {
    if (= a (-0)) return 0
    else if (< a 0) return (- a)
    else return a
  }""")
}
