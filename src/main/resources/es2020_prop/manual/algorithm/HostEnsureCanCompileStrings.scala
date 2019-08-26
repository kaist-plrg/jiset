package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object HostEnsureCanCompileStrings {
  val func: Func = parseFunc(""""HostEnsureCanCompileStrings" (callerRealm, calleRealm) => {
    return (new Completion("Type" -> CONST_normal, "Value" -> undefined, "Target" -> CONST_empty))
  }""")
}
