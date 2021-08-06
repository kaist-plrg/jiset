package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._

// view abstraction for analysis sensitivities
trait View extends AnalyzerElem {
  // call transition
  def doCall(call: Call): View = this match {
    case CallSiteView(calls) => CallSiteView(call :: calls)
    case _ => this
  }

  // loop transition
  def loopNext: View = this match {
    case LoopView((branch, k) :: rest) => LoopView((branch, k + 1) :: rest)
    case _ => this
  }
  def loopEnter(branch: Branch): View = this match {
    case LoopView(pairs) => LoopView((branch, 0) :: pairs)
    case _ => this
  }
  def loopExit: View = this match {
    case LoopView(_ :: rest) => LoopView(rest)
    case _ => this
  }
}
trait ViewGen[V <: View] { val base: V }

// insensitive views
object BaseView extends View

// callsite sensitivity
case class CallSiteView(calls: List[Call]) extends View
object CallSiteView extends ViewGen[CallSiteView] {
  val base = CallSiteView(Nil)
}

// loop sensitivity
case class LoopView(pairs: List[(Branch, Int)]) extends View
object LoopView extends ViewGen[LoopView] {
  val base = LoopView(Nil)
}
