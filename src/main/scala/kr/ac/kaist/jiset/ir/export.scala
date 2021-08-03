package kr.ac.kaist.jiset.ir

import io.circe._, io.circe.syntax._, io.circe.parser._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.util.Span
import scala.scalajs.js.annotation._

object Export {
  @JSExportTopLevel("Scala_WebDebugger")
  @JSExportAll
  class WebDebugger(override val st: State) extends Debugger {
    detail = true
    // ir steps
    def irStep() = step
    def irStepOver() = stepOver
    def irStepOut() = stepOut

    // spec steps
    def specStep() = {
      val (n0, l0) = st.context.getInfo()
      stepUntil {
        val (n1, l1) = st.context.getInfo()
        n0 == n1 && l0 == l1
      }
    }
    def specStepOver() = {
      val (n0, l0) = st.context.getInfo()
      val stackSize = st.ctxtStack.size
      stepUntil {
        val (n1, l1) = st.context.getInfo()
        (n0 == n1 && l0 == l1) || (stackSize != st.ctxtStack.size)
      }
    }
    def specStepOut() = stepOut

    // get stack frame info
    def getStackFrame(): String = {
      val stackFrame = st.context.getInfo() :: st.ctxtStack.map(_.getInfo(true))
      stackFrame.asJson.noSpaces
    }

    // get js range info
    def getJsRange(): String = {
      val range: (Int, Int) = (st.context :: st.ctxtStack).foldLeft((-1, -1)) {
        case ((-1, -1), context) if context.isAstEvaluation =>
          val ast = context.astOpt.get
          val Span(start, end) = ast.span
          (start.index, end.index)
        case (acc, _) => acc
      }
      range.asJson.noSpaces
    }
  }

  @JSExportTopLevel("Scala_initializeState")
  def initializeDebugger(compressed: String): State = {
    val json = parse(compressed) match {
      case Left(err) => throw err
      case Right(json) => json
    }
    val script = Script(json)
    Initialize(script, cursorGen = NodeCursor)
  }

  @JSExportTopLevel("Scala_setSpec")
  def setSpec(raw: String): Unit = for {
    json <- parse(raw)
    spec <- json.as[ECMAScript]
  } yield setTarget(spec)
}
