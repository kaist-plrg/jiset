package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object RegExpCreate {
  val func: Func = parseFunc(""""RegExpCreate" (pattern, flags) => {
    !!! "RegularExpression"
  }""")
}
