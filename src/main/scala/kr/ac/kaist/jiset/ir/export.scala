package kr.ac.kaist.jiset.ir

import io.circe._, io.circe.syntax._, io.circe.parser._
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js.{ setSpec => setJsSpec, _ }
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.util.Span
import scala.scalajs.js.annotation._

object Export {
  @JSExportTopLevel("Scala_WebDebugger")
  @JSExportAll
  class WebDebugger(override val st: State) extends Debugger {
    type StepResult = Debugger.StepResult
    detail = true
    // ir steps
    def irStep(): Int = step.id
    def irStepOver(): Int = stepOver.id
    def irStepOut(): Int = stepOut.id

    // spec steps
    def specStep(): Int = {
      val (n0, l0, _) = st.context.getInfo()
      stepUntil {
        val (n1, l1, _) = st.context.getInfo()
        n0 == n1 && l0 == l1
      }.id
    }
    def specStepOver(): Int = {
      val (n0, l0, _) = st.context.getInfo()
      val stackSize = st.ctxtStack.size
      stepUntil {
        val (n1, l1, _) = st.context.getInfo()
        (n0 == n1 && l0 == l1) || (stackSize != st.ctxtStack.size)
      }.id
    }
    def specStepOut(): Int = stepOut.id

    // continue
    def continueAlgo(): Int = continue.id

    // breakpoints
    def addAlgoBreak(algoName: String, enabled: Boolean = true) = addBreak(algoName, enabled)
    def rmAlgoBreak(opt: String) = rmBreak(opt)
    def toggleAlgoBreak(opt: String) = toggleBreak(opt)

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

    // get heap info
    def getHeap(): String = st.heap.map.map {
      case (addr, obj) => (addr.toString, obj.toString)
    }.asJson.noSpaces

    def getEnv(): String = getFullEnv().asJson.noSpaces
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
  } yield setJsSpec(spec)
}
