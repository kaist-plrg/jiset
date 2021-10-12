package kr.ac.kaist.jiset.js

import io.circe._, io.circe.syntax._, io.circe.parser._
import kr.ac.kaist.jiset.ir.{ Debugger => IRDebugger, _ }
import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.spec.JsonProtocol._
import scala.collection.mutable.{ Map => MMap }

// ECMAScript Debugger
class Debugger(override val st: State) extends IRDebugger {
  type StepResult = Debugger.StepResult
  detail = true
  // spec steps
  def specStep(): StepResult = decorate {
    val (n0, l0, _) = st.context.getInfo()
    stepUntil {
      val (n1, l1, _) = st.context.getInfo()
      n0 == n1 && l0 == l1
    }
  }
  def specStepOver(): StepResult = decorate {
    val (n0, l0, _) = st.context.getInfo()
    val stackSize = st.ctxtStack.size
    stepUntil {
      val (n1, l1, _) = st.context.getInfo()
      (n0 == n1 && l0 == l1) || (stackSize != st.ctxtStack.size)
    }
  }
  def specStepOut(): StepResult = decorate(stepOut)

  // spec continue
  def specContinue(): StepResult = decorate(continue)

  // decorate StepResult with state information
  private def decorate(
    result: IRDebugger.StepResult
  ): Debugger.StepResult = Debugger.StepResult(
    result.id,
    st.context.getInfo() :: st.ctxtStack.map(_.getInfo(true)),
    st.getJSInfo(),
    st.heap.map.map {
      case (addr, obj) => (addr.toString, obj.toString)
    }
  )

  // TODO JS
  // def jsStep(): Int = {
  //   val (lPrev0, _, _, _) = st.getJSInfo()
  //   stepUntil {
  //     val (lNext0, lNext1, _, _) = st.getJSInfo()
  //     val (n, _, _) = st.context.getInfo()
  //     (lNext0 != lNext1) || (lPrev0 == lNext0) || ((lPrev0 > 0) && (lNext0 <= 0))
  //   }.id

  // def addJSBreak(line: Int, enabled: Boolean = true) = addBreakJS(line, enabled)
  // def rmJSBreak(opt: String) = rmBreakJS(opt)
  // def toggleJSBreak(opt: String) = toggleBreakJS(opt)
  // def getEnv(): String = getFullEnv().asJson.noSpaces
}

object Debugger {
  type StackFrameInfo = (String, Int, List[(String, String)])
  // decorated step result
  case class StepResult(
    result: Int,
    stackFrames: List[StackFrameInfo],
    jsRanges: (Int, Int, Int, Int),
    heap: MMap[String, String]
  )
}
