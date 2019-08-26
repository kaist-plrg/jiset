package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object HostHasSourceTextAvailable {
  val func: Func = parseFunc(""""HostHasSourceTextAvailable" (func) => {
    return (new Completion("Type" -> CONST_normal, "Value" -> true, "Target" -> CONST_empty))
  }""")
}
