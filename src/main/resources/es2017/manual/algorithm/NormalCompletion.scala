package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object NormalCompletion {
  val func: Func = parseFunc(""""NormalCompletion" (argument) => {
    return (new Completion(
      "Type" -> CONST_normal,
      "Value" -> argument,
      "Target" -> CONST_empty
    ))
  }""")
}
