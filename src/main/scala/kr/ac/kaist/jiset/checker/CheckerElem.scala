package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.checker.Beautifier._

// type checker components
trait CheckerElem {
  // conversion to string
  def beautified: String = beautify(this)
}
