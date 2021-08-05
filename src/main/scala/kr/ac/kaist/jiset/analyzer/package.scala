package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.cfg._

package object analyzer {
  // options
  var USE_REPL: Boolean = false

  // path type
  type Path = List[NodePoint[Call]]
}
