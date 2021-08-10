package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.js.ast._

// view abstraction for analysis sensitivities
case class View(
  jsView: JSView = JSBase,
  irCtxts: List[Ctxt] = Nil
) extends AnalyzerElem {
  // call transition
  def doCall(call: Call, astOpt: Option[AST]): View = astOpt match {
    case Some(ast) => {
      // TODO flow sensitivity
      View(JSFlow(ast), Nil)

      // TODO infinite sensitivity
      // View(JSFlow(ast), CallCtxt(call) :: irCtxts)
    }
    case None => copy(irCtxts = CallCtxt(call) :: irCtxts)
  }

  // loop transition
  def loopNext: View = irCtxts match {
    case (LoopCtxt(loop, k) :: rest) =>
      copy(irCtxts = LoopCtxt(loop, k + 1) :: rest)
    case _ => this
  }
  def loopEnter(loop: Loop): View = copy(irCtxts = LoopCtxt(loop, 0) :: irCtxts)
  def loopExit: View = irCtxts match {
    case LoopCtxt(_, _) :: rest => copy(irCtxts = rest)
    case _ => this
  }

  // getter
  def calls: List[CallCtxt] = irCtxts.collect { case call: CallCtxt => call }
  def loops: List[LoopCtxt] = irCtxts.collect { case loop: LoopCtxt => loop }
}

// contexts
sealed trait Ctxt
case class LoopCtxt(loop: Loop, depth: Int) extends Ctxt
case class CallCtxt(call: Call) extends Ctxt

// views for JavaScript
sealed trait JSView
case object JSBase extends JSView
case class JSFlow(ast: AST) extends JSView
