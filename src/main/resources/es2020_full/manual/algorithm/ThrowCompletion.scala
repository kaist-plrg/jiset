package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object ThrowCompletion {
  val func: Func = parseFunc(""""ThrowCompletion" (argument) => {
    return (new Completion(
      "Type" -> CONST_throw,
      "Value" -> argument,
      "Target" -> CONST_empty
    ))
  }""")
}
