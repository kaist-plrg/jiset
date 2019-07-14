package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object IsArrayIndex {
  val func: Func = parseFunc(""""IsArrayIndex" (P) => {
    let u = (ToUint32 P)
    if (= u 4294967295i) return false else {}
    let s = (ToString u)
    return (= s P)
  }""")
}
