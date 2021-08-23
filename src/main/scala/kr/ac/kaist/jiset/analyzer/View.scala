package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.js.ast._

// view abstraction for analysis sensitivities
case class View(
  jsViewOpt: Option[JSView] = None,
  calls: List[Call] = Nil,
  loops: List[LoopCtxt] = Nil,
  intraLoopDepth: Int = 0
) extends AnalyzerElem {
  // call transition
  def doCall(
    call: Call,
    isJsCall: Boolean,
    astOpt: Option[AST]
  ): View = {
    val view = copy(calls = call :: calls, intraLoopDepth = 0)
    if (JS_SENS) view.jsSens(isJsCall, astOpt)
    else view
  }

  // JavaScript sensitivities
  def jsSens(isJsCall: Boolean, astOpt: Option[AST]): View = astOpt match {
    // flow sensitivity
    case Some(ast) =>
      View(Some(JSView(ast, jsCalls, loops ++ jsLoops)), Nil, Nil, 0)
    // call-site sensitivity
    case _ if isJsCall => copy(jsViewOpt = jsViewOpt.map {
      case JSView(ast, calls, loops) => JSView(ast, ast :: calls, loops)
    })
    // non-JS part
    case _ => this
  }

  // get JavaScript contexts
  def jsCalls: List[AST] = jsViewOpt.fold(List[AST]())(_.calls)
  def jsLoops: List[LoopCtxt] = jsViewOpt.fold(List[LoopCtxt]())(_.loops)

  // loop transition
  def loopNext: View = loops match {
    case LoopCtxt(loop, k) :: rest =>
      copy(loops = LoopCtxt(loop, k + 1) :: rest)
    case _ => this
  }
  def loopEnter(loop: Loop): View = copy(
    loops = LoopCtxt(loop, 0) :: loops,
    intraLoopDepth = intraLoopDepth + 1,
  )
  def loopExit: View = loops match {
    case LoopCtxt(_, _) :: rest => copy(
      loops = rest,
      intraLoopDepth = intraLoopDepth - 1,
    )
    case _ => this
  }

  // get entry views
  def entryView: View = copy(
    loops = loops.drop(intraLoopDepth),
    intraLoopDepth = 0,
  )
}

// contexts
case class LoopCtxt(loop: Loop, depth: Int)

// views for JavaScript
case class JSView(
  ast: AST,
  calls: List[AST],
  loops: List[LoopCtxt]
)
