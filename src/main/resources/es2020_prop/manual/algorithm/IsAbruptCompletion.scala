package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object IsAbruptCompletion {
  val func: Func = parseFunc(""""IsAbruptCompletion" (x) => {
    return (&& (= (typeof x) "Completion") (! (= x.Type CONST_normal)))
  }""")
}
