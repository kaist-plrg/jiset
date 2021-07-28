package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.spec.algorithm.{ Algo, Param }

// origins of CFG functions
sealed trait Origin {
  // get name from origins
  def name: String = this match {
    case AlgoOrigin(algo) => algo.name
  }

  // get parameters from origins
  def params: List[Param] = this match {
    case AlgoOrigin(algo) => algo.params
  }
}
case class AlgoOrigin(algo: Algo) extends Origin
// TODO other origins (e.g. clo, cont)
