package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object ThrowCompletion {
  val func: Func = parseFunc(""""WrapCompletion" (argument) => {
    return (new Completion(
      "Type" -> throw,
      "Value" -> argument,
      "Target" -> empty
    ))
  }""")
}
