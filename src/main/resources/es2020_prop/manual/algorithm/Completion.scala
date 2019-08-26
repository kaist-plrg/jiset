package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object Completion {
  val func: Func = parseFunc(""""Completion" (argument) => {
    return argument
  }""")
}
