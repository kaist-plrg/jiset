package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.analyzer.domain._

object Initialize {
  // initial abstract state
  val init: AbsState = AbsState.Empty

  // get initial abstract state for syntax-directed algorithms
  def apply(head: SyntaxDirectedHead): AbsState = init
}
