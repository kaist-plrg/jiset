package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object floor {
  val func: Func = parseFunc(""""floor" (a) => {
    return (- a (% a 1))
  }""")
}
