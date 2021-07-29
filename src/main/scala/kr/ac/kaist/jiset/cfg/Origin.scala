package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.spec.algorithm.{ Algo, Param }
import kr.ac.kaist.jiset.ir._

// origins of CFG functions
sealed trait Origin extends CFGComponent {
  // algorithm
  val algo: Algo

  // body instruction
  def body: Inst = this match {
    case AlgoOrigin(algo) => algo.body
    case ArrowOrigin(_, arrow) => arrow.body
  }

  // get name from origins
  def name: String = this match {
    case AlgoOrigin(algo) => algo.name
    case ArrowOrigin(algo, arrow) => s"${algo.name}:${arrow.line.getOrElse(-1)}"
  }

  // get parameters from origins
  def params: List[Param] = this match {
    case AlgoOrigin(algo) => algo.params
    case ArrowOrigin(_, arrow) => arrow.params.map(x => Param(x.name))
  }
}

// algorithm origins
case class AlgoOrigin(algo: Algo) extends Origin

// arrow origins for closures and continuations
case class ArrowOrigin(algo: Algo, arrow: ArrowInst) extends Origin
