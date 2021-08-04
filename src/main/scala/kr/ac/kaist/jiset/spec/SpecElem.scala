package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec.Beautifier._

// specification components
trait SpecElem {
  // conversion to string
  def beautified: String = beautify(this)
}
