package kr.ac.kaist.ase.error

sealed abstract class AlgorithmError(msg: String) extends ASEError(msg)

case object AlgorithmNotYetGenerated extends AlgorithmError({
  s"Algorithms are not yet generated. Please generate algorithm parsers using './gen-algo-parser'."
})
