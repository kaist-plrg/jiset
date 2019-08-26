package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object IsAbruptCompletion {
  val func: Func = parseFunc(""""IsAbruptCompletion" (x) => {
    return (&& (= (typeof x) "Completion") (! (= x.Type CONST_normal)))
  }""")
}
