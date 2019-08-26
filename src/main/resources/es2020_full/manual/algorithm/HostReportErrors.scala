package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object HostReportErrors {
  val func: Func = parseFunc(""""HostReportErrors" (errorList) => {
    assert "Error occured"
    return (new Completion("Type" -> CONST_normal, "Value" -> undefined, "Target" -> CONST_empty))
  }""")
}
