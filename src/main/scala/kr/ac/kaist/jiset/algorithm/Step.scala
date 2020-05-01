package kr.ac.kaist.jiset.algorithm

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.parser._

// steps
case class Step(var tokens: List[Token]) {
  def getSteps(init: List[Step]): List[Step] = tokens.foldLeft(this :: init) {
    case (list, StepList(steps)) => steps.foldLeft(list) {
      case (list, s) => s.getSteps(list)
    }
    case (list, _) => list
  }
  def getSteps: List[Step] = getSteps(Nil).reverse
}
