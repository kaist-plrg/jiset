package kr.ac.kaist.ase.error

import kr.ac.kaist.ase.algorithm._

sealed abstract class AlgorithmError(msg: String) extends ASEError(msg)

case object AlgorithmNotYetGenerated extends AlgorithmError({
  s"Algorithms are not yet generated. Please generate algorithm parsers using 'sbt generateModel'."
})

case class UnexpectedToken(token: Token) extends AlgorithmError({
  s"Unexpected: $token"
})
