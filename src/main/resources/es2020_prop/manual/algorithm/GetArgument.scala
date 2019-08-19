package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object GetArgument {
  val func: Func = parseFunc(""""GetArgument" (argumentsList, idx) => {
    let arg = argumentsList[idx]
    if (= arg absent) return undefined
    else return arg
  }""")
}
