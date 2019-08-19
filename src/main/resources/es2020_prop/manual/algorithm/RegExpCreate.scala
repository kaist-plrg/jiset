package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object RegExpCreate {
  val func: Func = parseFunc(""""RegExpCreate" (pattern, flags) => {
    !!! "RegularExpression"
  }""")
}
