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
// TODO JS breakpoint, JS state
class Debugger(override val st: State) extends IRDebugger {
  type StepResult = Debugger.StepResult
  detail = false

  // spec step
  def specStep(): StepResult = decorate {
    val (n0, l0, _) = st.context.getInfo()
    stepUntil {
      val (n1, l1, _) = st.context.getInfo()
      n0 == n1 && l0 == l1
    }
  }

  // spec step-over
  def specStepOver(): StepResult = decorate {
    val (n0, l0, _) = st.context.getInfo()
    val stackSize = st.ctxtStack.size
    stepUntil {
      val (n1, l1, _) = st.context.getInfo()
      (n0 == n1 && l0 == l1) || (stackSize != st.ctxtStack.size)
    }
  }

  // spec step-out
  def specStepOut(): StepResult = decorate(stepOut)

  // spec continue
  def specContinue(): StepResult = decorate(continue)

  // js step
  def jsStep(): StepResult = decorate {
    val (sl0, el0, _, _) = st.getJsPos()
    val isPrevSingle = isSingleLine(sl0, el0)
    stepUntil {
      val (sl1, el1, _, _) = st.getJsPos()
      val isSingle = isSingleLine(sl1, el1)
      // if start pos is not valid or not single line
      // run until pos becomes valid single line
      if (!isPrevSingle) !isSingle
      // else, run until pos becomes next line
      else !isSingle || sl0 == sl1
    }
  }

  // js step-out
  def jsStepOut(): StepResult = decorate {
    val c0 = countJsCall
    stepUntil { c0 <= countJsCall }
  }

  // js step-over
  def jsStepOver(): StepResult = decorate {
    val c0 = countJsCall
    val (sl0, el0, _, _) = st.getJsPos()
    val isPrevSingle = isSingleLine(sl0, el0)
    stepUntil {
      val (sl1, el1, _, _) = st.getJsPos()
      val isSameJsDepth = c0 == countJsCall
      val isSingle = isSingleLine(sl1, el1)
      // if js call depth is same,
      if (isSameJsDepth) {
        // if start pos is not valid or not single line
        // run until pos becomes valid single line
        if (!isPrevSingle) !isSingle
        // else, run until pos becomes next line
        else !isSingle || sl0 == sl1
      } // otherwise, run until js call depth becomes same
      else true
    }
  }

  // decorate StepResult with state information
  private def decorate(
    result: IRDebugger.StepResult
  ): Debugger.StepResult = Debugger.StepResult(
    result.id,
    st.context.getInfo() :: st.ctxtStack.map(_.getInfo(true)),
    st.getJsPos(),
    st.heap.map.map {
      case (addr, obj) => (addr.toString, obj.toString)
    }
  )

  // helpers
  def isValidLine(line: Int): Boolean = line != -1
  def isSingleLine(l0: Int, l1: Int): Boolean =
    isValidLine(l0) && isValidLine(l1) && l0 == l1
  def countJsCall: Int =
    (st.context :: st.ctxtStack).filter(_.isJsCall).size
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
