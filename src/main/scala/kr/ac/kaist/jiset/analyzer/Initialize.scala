package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.analyzer.domain._

object Initialize {
  // initial abstract state
  val init: AbsState = AbsState.Empty

  // get initial abstract state for syntax-directed algorithms
  def apply(head: SyntaxDirectedHead): AbsState = {
    var argMap = Map[String, AbsValue]()
    if (head.subIdx == 0) {
      argMap += (THIS_PARAM -> AbsAST(ASTVal(head.lhsName)))
      head.rhs.getNTs.foreach(nt => {
        val name = nt.name
        argMap += (name -> AbsAST(ASTVal(name)))
      })
    } else ???
    argMap.foldLeft(init) {
      case (st, (param, arg)) => st.define(param, arg)
    }
  }
}
