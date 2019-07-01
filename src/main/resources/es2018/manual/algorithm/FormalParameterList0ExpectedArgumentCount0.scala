package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object FormalParameterList0ExpectedArgumentCount0 {
  val func: Func = parseFunc(""""FormalParameterList0ExpectedArgumentCount0" (FormalParameter) => {
    if (= FormalParameter.HasInitializer true) return 0
    return 1
  }""")
}
