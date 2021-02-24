package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.ir.state.ASTVal

object Initialize {
  // initial abstract state
  val init: AbsState = AbsState.Empty

  // initial abstract state for syntax-directed algorithms
  def apply(head: SyntaxDirectedHead): List[(List[Type], AbsState)] =
    head.optional.subsets.map(opt => {
      var st = init
      val types: List[Type] = head.types.map {
        case (name, _) if opt contains name =>
          st += name -> AbsAbsent.Top
          AbsentT
        case (name, astName) =>
          st += name -> AbsAST(ASTVal(astName))
          AstT(astName)
      }
      (types, st)
    }).toList
}
