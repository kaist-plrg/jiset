package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object HostPromiseRejectionTracker {
  val func: Func = parseFunc(""""HostPromiseRejectionTracker" (promise, operation) => {
    return (new Completion("Type" -> CONST_normal, "Value" -> undefined, "Target" -> CONST_empty))
  }""")
}
