package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._

// view abstraction for analysis sensitivities
case class View(
  ctxts: List[Ctxt] = Nil
) extends AnalyzerElem {
  // call transition
  def doCall(call: Call): View = copy(CallCtxt(call) :: ctxts)

  // loop transition
  def loopNext: View = ctxts match {
    case (LoopCtxt(loop, k) :: rest) => View(LoopCtxt(loop, k + 1) :: rest)
    case _ => this
  }
  def loopEnter(loop: Loop): View = View(LoopCtxt(loop, 0) :: ctxts)
  def loopExit: View = ctxts match {
    case LoopCtxt(_, _) :: rest => View(rest)
    case _ => this
  }

  // getter
  def calls: List[CallCtxt] = ctxts.collect { case call: CallCtxt => call }
  def loops: List[LoopCtxt] = ctxts.collect { case loop: LoopCtxt => loop }
}

// contexts
sealed trait Ctxt
case class LoopCtxt(loop: Loop, depth: Int) extends Ctxt
case class CallCtxt(call: Call) extends Ctxt
