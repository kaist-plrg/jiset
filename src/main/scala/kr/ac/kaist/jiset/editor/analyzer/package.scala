package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.error.AnalysisImprecise

package object analyzer {
  // options
  val USE_REPL: Boolean = false
  var ANALYZE_TIMEOUT: Long = 20

  // path type
  type Path = List[NodePoint[Call]]
}
