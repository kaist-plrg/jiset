package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object abs {
  val func: Func = parseFunc(""""abs" (a) => {
    if (= a (-0)) return 0
    else if (< a 0) return (- a)
    else return a
  }""")
}
