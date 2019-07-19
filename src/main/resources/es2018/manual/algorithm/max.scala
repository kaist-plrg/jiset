package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object max {
  val func: Func = parseFunc(""""max" (...args) => {
    let x = 1i
    let len = (length-of args)
    let res = args[0i]
    while (< x len) {
      let v = args[x]
      if (< res v) res = v
      else {}
      x = (+ x 1i)
    }
    return res
  }""")
}
