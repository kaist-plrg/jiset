package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object GetArgument {
  val func: Func = parseFunc(""""GetArgument" (argumentsList, idx) => {
    let arg = argumentsList[idx]
    if (= arg absent) return undefined
    else return arg
  }""")
}
