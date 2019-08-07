package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object HostHasSourceTextAvailable {
  val func: Func = parseFunc(""""HostHasSourceTextAvailable" (func) => {
    return (new Completion("Type" -> CONST_normal, "Value" -> true, "Target" -> CONST_empty))
  }""")
}
