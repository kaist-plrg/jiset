package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.analyzer.Beautifier._

// analyzer components
trait AnalyzerComponent {
  override def toString: String = beautify(this)
}
