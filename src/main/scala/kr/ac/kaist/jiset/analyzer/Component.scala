package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.analyzer.Beautifier._

trait Component {
  override def toString: String = beautify(this)
}
