package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object min {
  val func: Func = parseFunc(""""min" (a, b) => {
    if (< a b) return a
    else return b
  }""")
}
