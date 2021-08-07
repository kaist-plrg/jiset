package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._

// view abstraction for analysis sensitivities
case class View(
  calls: List[Call] = Nil,
  loops: List[(Loop, Int)] = Nil
) extends AnalyzerElem {
  // call transition
  def doCall(call: Call): View = copy(calls = call :: calls)

  // loop transition
  def loopNext: View = loops match {
    case (loop, k) :: rest => copy(loops = (loop, k + 1) :: rest)
    case _ => this
  }
  def loopEnter(loop: Loop): View = copy(loops = (loop, 0) :: loops)
  def loopExit: View = loops match {
    case _ :: rest => copy(loops = rest)
    case _ => this
  }
}
