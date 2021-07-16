package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.{ DEBUG, TIMEOUT }
import scala.annotation.tailrec

// IR runtime
private class Runtime(
  st: State,
  timeLimit: Option[Long] = Some(TIMEOUT)
) {
  // interp object
  val interp = new Interp(st)

  // set start time of interpreter
  val startTime: Long = System.currentTimeMillis

  // the number of instructions
  def getInstCount: Int = instCount
  var instCount: Int = 0

  // iteration period for check
  val CHECK_PERIOD = 10000
  
  // step target 
  trait StepTarget
  case object Terminate extends StepTarget
  case object ReturnUndef extends StepTarget
  case class NextInst(inst: Inst, rest: List[Inst]) extends StepTarget

  // get next step target
  def nextTarget: StepTarget = st.context.insts match {
    case Nil => st.ctxtStack match {
      case Nil => Terminate
      case _ => ReturnUndef 
    }
    case inst :: rest => NextInst(inst, rest)
  }
  
  // step
  final def step: Boolean = nextTarget match {
    case Terminate => false
    case ReturnUndef => interp.doReturn(Undef); true
    case NextInst(inst, rest) => {
      // print stat
      instCount += 1
      if (instCount % CHECK_PERIOD == 0) timeLimit.map(limit => {
        val duration = (System.currentTimeMillis - startTime) / 1000
        if (duration > limit) error("TIMEOUT")
      })
      if (DEBUG) inst match {
        case ISeq(_) =>
        case _ => println(s"${st.context.name}: ${inst.beautified}")
      }

      // interp inst
      st.context.insts = rest
      interp.interp(inst)
      if (instCount % 100000 == 0) GC.gc(st)
      true
    }
  }

  // fixpoint
  @tailrec
  final def fixpoint: State = step match {
    case true => fixpoint
    case false => st
  }
}

object Runtime {
  def apply(
    st: State,
    timeLimit: Option[Long] = Some(TIMEOUT)
  ): State = {
    val runtime = new Runtime(st, timeLimit)
    runtime.fixpoint
  }
}
