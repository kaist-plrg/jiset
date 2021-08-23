package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.cfg._

package object analyzer {
  // options
  var USE_REPL: Boolean = false
  var USE_GC: Boolean = false
  var JS_SENS: Boolean = false
  var ANALYZE_TIMEOUT: Long = 20

  // path type
  type Path = List[NodePoint[Call]]
}
