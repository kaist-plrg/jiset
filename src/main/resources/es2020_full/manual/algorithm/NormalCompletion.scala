package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object NormalCompletion {
  val func: Func = parseFunc(""""NormalCompletion" (argument) => {
    return (new Completion(
      "Type" -> CONST_normal,
      "Value" -> argument,
      "Target" -> CONST_empty
    ))
  }""")
}
