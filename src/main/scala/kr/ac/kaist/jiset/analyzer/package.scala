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
  var JS_CALL_DEPTH: Int = 5
  var IR_CALL_DEPTH: Int = 50

  // Exploded
  def exploded(msg: String = ""): Nothing = throw AnalysisImprecise(msg)

  // measure parse time
  var __TOTAL_PARSE_TIME__ : Option[Long] = None
  def __INIT_PARSE_TIME__ : Unit = { __TOTAL_PARSE_TIME__ = Some(0) }
  def __GET_TOTAL_PARSE_TIME__ : Double = {
    val res = __TOTAL_PARSE_TIME__.get
    __TOTAL_PARSE_TIME__ = None
    res / 1000.0d
  }
  def __ADD_PARSE_TIME__(t: Long): Unit = if (!__TOTAL_PARSE_TIME__.isEmpty) {
    __TOTAL_PARSE_TIME__ = Some(__TOTAL_PARSE_TIME__.get + t)
  }

  // path type
  type Path = List[NodePoint[Call]]
}
