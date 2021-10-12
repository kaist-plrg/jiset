package kr.ac.kaist.jiset

import io.circe._, io.circe.syntax._, io.circe.parser._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.ir.{ State, Breakpoint, NodeCursor }
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.JsonProtocol._

package object web {
  // current running debugger
  private var _debugger: Option[Debugger] = None
  def debugger: Debugger = _debugger.get

  // set debugger based on given AST
  def setDebugger(bps: List[Breakpoint], compressed: String): Unit = {
    // decompress AST
    val script = parse(compressed) match {
      case Left(err) => throw err
      case Right(json) => Script(json)
    }
    // initialize state
    val initSt = Initialize(script, cursorGen = NodeCursor)
    // set current debugger
    _debugger = Some(new Debugger(initSt))
    // add initial breakpoints
    bps.foreach(debugger.addBreak(_))
  }
}
