package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.{ DEBUG, TIMEOUT }
import kr.ac.kaist.jiset.spec.algorithm._
import scala.collection.mutable.ArrayBuffer
import scala.annotation.tailrec

// Debugger breakpoint
trait BreakPoint {
  private var trigger = false
  def needTrigger: Boolean = {
    if (trigger) { trigger = false; true }
    else false
  }
  protected def on: Unit = trigger = true
  def check(str: String): Unit
}
case class AlgoBreakPoint(name: String) extends BreakPoint {
  override def check(str: String): Unit = if (name == str) this.on
}

// IR Debugger
trait Debugger {
  val st: State
  val interp = new Interp(st, None, true)
  val detail: Boolean
  var currentAlgo: Option[Algo] = None
  var currentInst: Option[Inst] = None

  // step until predicate
  @tailrec
  private def stepUntil(pred: => Boolean): Unit = {
    DEBUG = true
    if (!isBreak) {
      currentAlgo = st.context.algo
      currentInst = st.context.currentInst
      val keep = interp.step
      if (pred && keep) stepUntil(pred)
      else DEBUG = false
    } else DEBUG = false
  }

  // step-over
  final def stepOver: Unit = {
    val stackSize = st.ctxtStack.size
    stepUntil { stackSize != st.ctxtStack.size }
  }

  // step-out
  final def stepOut: Unit = {
    val stackSize = st.ctxtStack.size
    stepUntil { stackSize <= st.ctxtStack.size }
  }

  // breakpoints
  val breakpoints = ArrayBuffer[(InterpHook, BreakPoint)]()

  // add break
  final def addBreak(algoName: String) = {
    val bp = AlgoBreakPoint(algoName)
    val hook = interp.subscribe(algoName, Interp.Event.Call, st => {
      st.context.algo match {
        case Some(algo) => bp.check(algo.name)
        case None =>
      }
    })
    breakpoints += ((hook, bp))
  }

  // remove break
  final def rmBreak(opt: String) = opt match {
    case "all" =>
      breakpoints.foreach { case (hook, _) => interp.unsubscribe(hook) }
      breakpoints.clear
    case idx => optional(idx.toInt) match {
      case Some(idx) if idx < breakpoints.size =>
        val (hook, _) = breakpoints(idx)
        breakpoints.remove(idx)
        interp.unsubscribe(hook)
      case None => error("wrong breakpoints index: $idx")
    }
  }

  // check if current step is in break
  final def isBreak: Boolean = breakpoints.foldLeft(false) {
    case (acc, (_, bp)) => bp.needTrigger || acc
  }

  // continue
  final def continue: Unit = stepUntil { true }

  // watch expressions
  var watchExprs = ArrayBuffer[Expr]()
  final def addExpr(exprStr: String) = {
    val expr = Expr(exprStr)
    watchExprs += expr
    println(evalExpr(expr))
    if (detail) println(s"${expr.beautified} added to watch list")
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

  // evaluate watch expressions
  private def evalExpr(expr: Expr): String = {
    try {
      val newSt = st.copied
      val interp = new Interp(newSt)
      interp.interp(expr) match {
        case addr: Addr =>
          val obj = newSt(addr)
          s"${addr.beautified}$LINE_SEP${obj.beautified}"
        case v => v.beautified
      }
    } catch { case _: Throwable => "ERROR" }
  }
  final def evalWatch: Unit =
    watchExprs.zipWithIndex.foreach {
      case (expr, i) =>
        val result = evalExpr(expr)
        println(f"$i: ${expr.beautified}%-20s $result")
    }
}
