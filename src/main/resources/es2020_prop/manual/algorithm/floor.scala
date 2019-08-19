package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object floor {
  val func: Func = parseFunc(""""floor" (a) => {
    return (- a (% a 1))
  }""")
}
