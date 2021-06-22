package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec.Beautifier._

trait SpecComponent {
  def beautified: String = beautify(this)
}
