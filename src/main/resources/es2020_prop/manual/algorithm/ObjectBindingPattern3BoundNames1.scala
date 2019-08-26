package kr.ac.kaist.jiset.model

import kr.ac.kaist.jiset.core.Parser._
import kr.ac.kaist.jiset.core._

object ObjectBindingPattern3BoundNames1 {
  val func: Func = parseFunc(""""ObjectBindingPattern3BoundNames1" (this, BindingPropertyList, BindingRestProperty) => {
    access __x0__ = (BindingPropertyList "BoundNames")
    let names = __x0__
    access __x1__ = (BindingRestProperty "BoundNames")
    let __x2__ = __x1__
    let __x3__ = 0i
    while (< __x3__ __x2__["length"]) {
      let __x4__ = __x2__[__x3__]
      append __x4__ -> names
      __x3__ = (+ __x3__ 1i)
    }
    return names
  }""")
}
