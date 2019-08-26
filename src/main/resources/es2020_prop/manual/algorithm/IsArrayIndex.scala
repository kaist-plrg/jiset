package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object IsArrayIndex {
  val func: Func = parseFunc(""""IsArrayIndex" (P) => {
    app u = (ToUint32 P)
    if (= u 4294967295i) return false else {}
    app s = (ToString u)
    return (= s P)
  }""")
}
