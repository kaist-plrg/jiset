package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.{ DEBUG, TIMEOUT }
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.cfg._
import scala.collection.mutable.ArrayBuffer
import scala.annotation.tailrec

// Debugger breakpoint
trait Breakpoint {
  var enabled: Boolean
  private var trigger = false
  def needTrigger: Boolean = {
    if (trigger) { trigger = false; true }
    else false
  }
  protected def on: Unit = trigger = true
  def check(str: String): Unit
  def toggle() = { enabled = !enabled }
}
case class AlgoBreakpoint(
  name: String,
  var enabled: Boolean = true
) extends Breakpoint {
  override def check(str: String): Unit =
    if (enabled && name == str) this.on
}
case class JSBreakpoint(
  line: Int,
  var enabled: Boolean = true
) extends Breakpoint {
  private var suppressed = false
  override def check(str: String): Unit =
    if (enabled) {
      val curLine = str.toInt
      if (line == curLine) {
        if (!suppressed) {
          this.on
          suppressed = true
        }
      } else if (curLine > 0 && suppressed) {
        suppressed = false
      }
    }
}

// IR Debugger
object Debugger {
  // Debugger step result
  type StepResult = StepResult.Value
  object StepResult extends Enumeration {
    val Break, Terminate, Success = Value
  }
}
trait Debugger {
  import Debugger._
  val st: State
  val interp = new Interp(st, None, true)
  var detail: Boolean = false

  // step until predicate
  @tailrec
  final def stepUntil(pred: => Boolean): StepResult = {
    DEBUG = true
    if (!isBreak) {
      val keep = interp.step
      if (pred && keep) stepUntil(pred)
      else {
        DEBUG = false
        if (keep) StepResult.Success else StepResult.Terminate
      }
    } else { DEBUG = false; StepResult.Break }
  }

  // step
  final def step: StepResult = stepUntil {
    interp.nextTarget match {
      case next: interp.NextStep => false
      case _ => true
    }
  }

  // step-over
  final def stepOver: StepResult = {
    val stackSize = st.ctxtStack.size
    stepUntil { stackSize != st.ctxtStack.size }
  }

  // step-out
  final def stepOut: StepResult = {
    val stackSize = st.ctxtStack.size
    stepUntil { stackSize <= st.ctxtStack.size }
  }

  // continue
  final def continue: StepResult = stepUntil { true }

  // breakpoints
  val breakpoints = ArrayBuffer[(Interp.Hook, Breakpoint)]()

  // check if current step is in break
  final def isBreak: Boolean = breakpoints.foldLeft(false) {
    case (acc, (_, bp)) => bp.needTrigger || acc
  }

  // get breakpoint by index
  private def getBreakIdx(
    bpList: ArrayBuffer[(Interp.Hook, Breakpoint)],
    idx: String
  ): Int =
    optional(idx.toInt) match {
      case Some(idx) if idx < bpList.size => idx
      case None => error("wrong breakpoints index: $idx")
    }

  // add break
  final def addBreak(bp: Breakpoint, name: Option[String] = None) = {
    val hook = interp.subscribe(Interp.Event.Call, st => {
      st.context.algo match {
        case Some(algo) => bp.check(algo.name)
        case None =>
      }
    }, name)
    breakpoints += ((hook, bp))
  }
  final def addAlgoBreak(algoName: String, enabled: Boolean = true) = {
    val bp = AlgoBreakpoint(algoName, enabled)
    addBreak(bp, Some(algoName))
  }

  // remove break
  final def rmBreak(opt: String) = opt match {
    case "all" =>
      breakpoints.foreach { case (hook, _) => interp.unsubscribe(hook) }
      breakpoints.clear
    case _ =>
      val idx = getBreakIdx(breakpoints, opt)
      val (hook, _) = breakpoints(idx)
      breakpoints.remove(idx)
      interp.unsubscribe(hook)
  }

  // toggle break
  final def toggleBreak(opt: String) = opt match {
    case "all" =>
      breakpoints.foreach { case (_, bp) => bp.toggle() }
    case _ =>
      val idx = getBreakIdx(breakpoints, opt)
      val (_, bp) = breakpoints(idx)
      bp.toggle()
  }

  // remove watch
  final def rmWatch(opt: String) = opt match {
    case "all" =>
      watchExprs.clear
    case idx => optional(idx.toInt) match {
      case Some(idx) if idx < watchExprs.size =>
        watchExprs.remove(idx)
      case None => error("wrong watch expressions index: $idx")
    }
  }

  // watch expressions
  var watchExprs = ArrayBuffer[Expr]()
  final def addExpr(exprStr: String) = {
    val expr = Expr(exprStr)
    watchExprs += expr
    println(evalExpr(expr))
    if (detail) println(s"$expr added to watch list")
  }

  // evaluate watch expressions
  private def evalExpr(expr: Expr): String = {
    try {
      val newSt = st.copied
      val interp = new Interp(newSt)
      interp.interp(expr) match {
        case addr: Addr =>
          val obj = newSt(addr)
          s"$addr$LINE_SEP$obj"
        case v => v.toString
      }
    } catch { case _: Throwable => "ERROR" }
  }
  final def evalWatch: Unit =
    watchExprs.zipWithIndex.foreach {
      case (expr, i) =>
        val result = evalExpr(expr)
        println(f"$i: $expr%-20s $result")
    }
}
