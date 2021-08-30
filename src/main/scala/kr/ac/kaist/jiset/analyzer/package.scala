package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.error.AnalysisImprecise

package object analyzer {
  // options
  var USE_REPL: Boolean = false
  var USE_GC: Boolean = false
  var INF_SENS: Boolean = false
  var ANALYZE_TIMEOUT: Long = 20

  // (i, j) for loop sensitivity
  var LOOP_ITER: Int = 100
  var LOOP_DEPTH: Int = 20

  // k for call-site sensitivity
  var JS_CALL_DEPTH: Int = 10
  var IR_CALL_DEPTH: Int = 50

  // Exploded
  def exploded(msg: String = "") = throw AnalysisImprecise(msg)

  // path type
  type Path = List[NodePoint[Call]]
}
