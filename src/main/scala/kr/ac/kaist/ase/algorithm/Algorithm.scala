package kr.ac.kaist.ase.algorithm

import java.io.Reader
import kr.ac.kaist.ase.LINE_SEP
import kr.ac.kaist.ase.parser._
import scala.util.parsing.combinator._

// algorithms
case class Algorithm(params: List[Param], stepList: StepList, filename: String) {
  def getSteps(init: List[Step]): List[Step] = (init /: stepList.steps) {
    case (list, step) => step.getSteps(list)
  }
}

// parsers
trait AlgorithmParsers extends JavaTokenParsers
    with RegexParsers
    with ParamParsers
    with StepParsers
    with StmtParsers
    with TokenParsers {
  lazy val algorithm: Parser[Algorithm] = tagged("algorithm", rep(param) ~ stepList) ^^ {
    case ps ~ sl => Algorithm(ps, sl, "no-file")
  }
}
object Algorithm extends AlgorithmParsers with ParseTo[Algorithm] {
  lazy val rule = algorithm
  def apply(reader: Reader, filename: String): Algorithm =
    Algorithm(reader).copy(filename = filename)
}
