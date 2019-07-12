package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core.Parser._
import kr.ac.kaist.ase.core._

object IsDuplicate {
  val func: Func = parseFunc(""""IsDuplicate" (list) => {
    let i = 0i
    let len = (length-of list)
    while (< i len) {
      let j = (+ i 1i)
      while (< j len) {
        if (= list[i] list[j]) return true
        else {}
        j = (+ j 1i)
      }
      i = (+ i 1i)
    }
    return false
  }""")
}
