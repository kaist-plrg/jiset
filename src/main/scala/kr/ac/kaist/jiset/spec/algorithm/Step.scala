package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.LINE_SEP

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
object Step {
  def toTokens(steps: List[Step]): List[Token] = {
    var k = 0
    def next: Next = { val res = Next(k); k += 1; res }
    def T(tokens: List[Token], token: Token): List[Token] = token match {
      case StepList(steps) => Out :: steps.foldLeft(In :: tokens)(S(_, _))
      case t => t :: tokens
    }
    def S(tokens: List[Token], step: Step): List[Token] =
      next :: step.tokens.foldLeft(tokens)(T(_, _))
    steps.foldLeft(List[Token]())(S(_, _)).reverse
  }
}
