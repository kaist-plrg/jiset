package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir.Inst
import kr.ac.kaist.jiset.spec.algorithm.{ Algo, Head, Param }
import kr.ac.kaist.jiset.util.{ UId, UIdGen }

// CFG functions
case class Function(
  uidGen: UIdGen[Function],
  origin: Origin,
  entry: Entry,
  exit: Exit,
  nodes: Set[Node],
  nexts: Map[Linear, Node],
  branches: Map[Branch, (Node, Node)],
  complete: Boolean
) extends CFGComponent with UId[Function] {
  // optionally get algorithm
  def algoOption: Option[Algo] = origin match {
    case AlgoOrigin(algo) => Some(algo)
    case _ => None
  }

  // optionally get algorithm head
  def headOption: Option[Head] = algoOption.map(_.head)

  // function name
  lazy val name: String = origin.name

  // function name
  lazy val params: List[Param] = origin.params

  // body instruction
  lazy val body: Inst = origin.body

  // conversion to DOT
  lazy val toDot: String = (new DotPrinter)(this).toString
}
