package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._

// type analysis result
case class AnalysisResult(
  npMap: Map[NodePoint[_ <: Node], AbsState],
  rpMap: Map[ReturnPoint, AbsType],
  thenBranches: Set[NodePoint[Branch]],
  elseBranches: Set[NodePoint[Branch]],
  retEdges: Map[ReturnPoint, Set[(NodePoint[Call], String)]],
  unknownVars: Set[(ControlPoint, String)],
  assertions: Map[ControlPoint, (AbsType, Expr)]
)
