package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.util.UId

// CFG functions
case class Function(
    entry: Entry,
    exit: Exit,
    nodes: Set[Node],
    forwards: Map[Node, Set[(Edge, Node)]]
) extends UId {
  // conversion to DOT
  def toDot: String = (new DotPrinter)(this).toString
}
