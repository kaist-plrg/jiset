package kr.ac.kaist.ase.algorithm

import kr.ac.kaist.ase.error.AlgorithmNotYetGenerated

trait Stmt extends Step
trait StmtParsers { this: AlgorithmParsers =>
  throw AlgorithmNotYetGenerated
  lazy val stmt: Parser[Stmt] = throw AlgorithmNotYetGenerated
}
