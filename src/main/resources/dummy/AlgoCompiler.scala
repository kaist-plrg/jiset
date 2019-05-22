package kr.ac.kaist.ase.model

import kr.ac.kaist.ase.core._
import kr.ac.kaist.ase.parser.TokenParsers
import kr.ac.kaist.ase.algorithm.Algorithm

object AlgoCompiler extends TokenParsers {
  def apply(algo: Algorithm): Func = Func(
    params = algo.params.map(Id(_)),
    body = ISeq(parse(rep(stmt), (algo.toTokenList)).get)
  )

  lazy val stmt = rep1(all) ^^ { case tokens => INotYetImpl("") }
}
