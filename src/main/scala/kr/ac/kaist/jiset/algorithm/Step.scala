package kr.ac.kaist.jiset.algorithm

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.parser._

// steps
case class Step(tokens: List[Token]) {
  def getSteps(init: List[Step]): List[Step] = ((this :: init) /: tokens) {
    case (list, StepList(steps)) => (list /: steps) {
      case (list, s) => s.getSteps(list)
    }
    case (list, _) => list
  }
  def getSteps: List[Step] = getSteps(Nil).reverse
}
