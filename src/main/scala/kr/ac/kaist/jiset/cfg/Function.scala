package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.spec.algorithm.{ Algo, Head }
import kr.ac.kaist.jiset.util.{ UId, UIdGen }

// CFG functions
case class Function(
  uidGen: UIdGen[Function],
  algo: Algo,
  entry: Entry,
  exit: Exit,
  nodes: Set[Node],
  edges: Set[Edge]
) extends UId[Function] {
  // completion check (not containing ??? or !!! in the algorithm body)
  lazy val complete: Boolean = algo.isComplete

  // algorithm head
  def head: Head = algo.head

  // function name
  def name: String = algo.name

  // conversion to DOT
  def toDot: String = (new DotPrinter)(this).toString
}
