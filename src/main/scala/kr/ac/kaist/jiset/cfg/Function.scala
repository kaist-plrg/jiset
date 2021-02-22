package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.jiset.util.UId

// CFG functions
case class Function(
  algo: Algo,
  entry: Entry,
  exit: Exit,
  nodes: Set[Node]
) extends UId {
  // connect nodes with function
  nodes.foreach(_._func = this)

  // function name
  def name: String = algo.name

  // conversion to DOT
  def toDot: String = (new DotPrinter)(this).toString
}
