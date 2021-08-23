package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.js.ast._

// view abstraction for analysis sensitivities
case class View(
  jsViewOpt: Option[JSView] = None,
  irCtxts: List[Ctxt] = Nil
) extends AnalyzerElem {
  // call transition
  def doCall(
    call: Call,
    isJsCall: Boolean,
    astOpt: Option[AST]
  ): View = {
    val view = copy(irCtxts = CallCtxt(call) :: irCtxts)
    if (JS_SENS) view.jsSens(isJsCall, astOpt)
    else view
  }

  // JavaScript sensitivities
  def jsSens(isJsCall: Boolean, astOpt: Option[AST]): View = astOpt match {
    // flow sensitivity
    case Some(ast) => View(Some(JSView(ast, jsCalls)), Nil)
    // call-site sensitivity
    case _ if isJsCall => copy(jsViewOpt = jsViewOpt.map {
      case JSView(ast, calls) => JSView(ast, ast :: calls)
    })
    // non-JS part
    case _ => this
  }

  // get JavaScript call stacks
  def jsCalls: List[AST] = jsViewOpt.fold(List[AST]())(_.calls)

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

  // get entry views
  def entryView: View = View(jsViewOpt, irCtxts.dropWhile(_ match {
    case _: LoopCtxt => true
    case _ => false
  }))

  // getter
  def calls: List[CallCtxt] = irCtxts.collect { case call: CallCtxt => call }
  def loops: List[LoopCtxt] = irCtxts.collect { case loop: LoopCtxt => loop }
}

// contexts
sealed trait Ctxt
case class LoopCtxt(loop: Loop, depth: Int) extends Ctxt
case class CallCtxt(call: Call) extends Ctxt

// views for JavaScript
case class JSView(ast: AST, calls: List[AST])
