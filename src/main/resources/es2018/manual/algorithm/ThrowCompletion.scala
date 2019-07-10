package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object ThrowCompletion {
  val func: Func = parseFunc(""""ThrowCompletion" (argument) => {
    return (new Completion(
      "Type" -> CONST_throw,
      "Value" -> argument,
      "Target" -> CONST_empty
    ))
  }""")
}
