package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object Completion {
  val func: Func = parseFunc(""""Completion" (argument) => {
    return argument
  }""")
}
