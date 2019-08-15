package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object HostPromiseRejectionTracker {
  val func: Func = parseFunc(""""HostPromiseRejectionTracker" (promise, operation) => {
    return (new Completion("Type" -> CONST_normal, "Value" -> undefined, "Target" -> CONST_empty))
  }""")
}
