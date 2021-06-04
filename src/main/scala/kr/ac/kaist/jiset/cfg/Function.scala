package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.jiset.util.{ UId, UIdGen }

// CFG functions
case class Function(
  uidGen: UIdGen,
  algo: Algo,
  entry: Entry,
  exit: Exit,
  nodes: Set[Node],
  edges: Set[Edge]
) extends UId {
  // completion check (not containing ??? or !!! in the algorithm body)
  val complete: Boolean = algo.isComplete

  // function name
  def name: String = algo.name

  // conversion to DOT
  def toDot: String = (new DotPrinter)(this).toString
}
