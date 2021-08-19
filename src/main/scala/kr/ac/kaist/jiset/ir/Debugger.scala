package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.{ DEBUG, TIMEOUT }
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.cfg._
import scala.collection.mutable.ArrayBuffer
import scala.annotation.tailrec

// Debugger breakpoint
trait BreakPoint {
  var enabled = true
  private var trigger = false
  def needTrigger: Boolean = {
    if (trigger) { trigger = false; true }
    else false
  }
  protected def on: Unit = trigger = true
  def check(str: String): Unit
  def toggle() = { enabled = !enabled }
}
case class AlgoBreakPoint(name: String) extends BreakPoint {
  override def check(str: String): Unit =
    if (enabled && name == str) this.on
}
case class JSBreakPoint(line: Int) extends BreakPoint {
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
    if (!isBreakAlgo && !isBreakJS) {
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
  val breakpointsAlgo = ArrayBuffer[(InterpHook, BreakPoint)]()
  val breakpointsJS = ArrayBuffer[(InterpHook, BreakPoint)]()

  // get breakpoint by index
  private def getBreakIdx(bpList: ArrayBuffer[(InterpHook, BreakPoint)], idx: String): Int =
    optional(idx.toInt) match {
      case Some(idx) if idx < bpList.size => idx
      case None => error("wrong breakpoints index: $idx")
    }

  // add break
  final def addBreak(algoName: String, enabled: Boolean = true) = {
    val bp = AlgoBreakPoint(algoName)
    val hook = interp.subscribe(algoName, Interp.Event.Call, st => {
      st.context.algo match {
        case Some(algo) => bp.check(algo.name)
        case None =>
      }
    })
    bp.enabled = enabled
    breakpointsAlgo += ((hook, bp))
  }

  // remove break
  final def rmBreak(opt: String) = opt match {
    case "all" =>
      breakpointsAlgo.foreach { case (hook, _) => interp.unsubscribe(hook) }
      breakpointsAlgo.clear
    case _ =>
      val idx = getBreakIdx(breakpointsAlgo, opt)
      val (hook, _) = breakpointsAlgo(idx)
      breakpointsAlgo.remove(idx)
      interp.unsubscribe(hook)
  }

  // toggle break
  final def toggleBreak(opt: String) = opt match {
    case "all" =>
      breakpointsAlgo.foreach { case (_, bp) => bp.toggle() }
    case _ =>
      val idx = getBreakIdx(breakpointsAlgo, opt)
      val (_, bp) = breakpointsAlgo(idx)
      bp.toggle()
  }

  // add break on JS
  final def addBreakJS(line: Int, enabled: Boolean = true) = {
    val bp = JSBreakPoint(line)
    val hook = interp.subscribe("", Interp.Event.Call, st => {
      val (l0, l1, _, _) = st.getJSInfo()
      if (l0 == l1) { bp.check(l0.toString) }
    })
    bp.enabled = enabled
    breakpointsJS += ((hook, bp))
  }

  // remove break on JS
  final def rmBreakJS(opt: String) = opt match {
    case "all" =>
      breakpointsJS.foreach { case (hook, _) => interp.unsubscribe(hook) }
      breakpointsJS.clear
    case _ =>
      val idx = getBreakIdx(breakpointsJS, opt)
      val (hook, _) = breakpointsJS(idx)
      breakpointsJS.remove(idx)
      interp.unsubscribe(hook)
  }
  // toggle break on JS
  final def toggleBreakJS(opt: String) = opt match {
    case "all" =>
      breakpointsJS.foreach { case (_, bp) => bp.toggle() }
    case _ =>
      val idx = getBreakIdx(breakpointsJS, opt)
      val (_, bp) = breakpointsJS(idx)
      bp.toggle()
  }

  // check if current step is in break
  final def isBreakAlgo: Boolean = breakpointsAlgo.foldLeft(false) {
    case (acc, (_, bp)) => bp.needTrigger || acc
  }
  final def isBreakJS: Boolean = breakpointsJS.foldLeft(false) {
    case (acc, (_, bp)) => bp.needTrigger || acc
  }

  // watch expressions
  var watchExprs = ArrayBuffer[Expr]()
  final def addExpr(exprStr: String) = {
    val expr = Expr(exprStr)
    watchExprs += expr
    println(evalExpr(expr))
    if (detail) println(s"$expr added to watch list")
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

  private def findAddrInHeap(from: Addr, key: String): Long =
    st.heap.apply(from, Str(key)) match {
      case DynamicAddr(addr) => addr
      case _ => -1L
    }

  private def getSubMapItems(addr: Addr): List[(String, String)] =
    st.heap.apply(addr) match {
      case (m: IRMap) => m.pairs.foldLeft(List[(String, String)]()) {
        case (env, (Str(name), DynamicAddr(nextAddr))) => {
          if (name != "arguments") {
            val value = st.heap.apply(DynamicAddr(nextAddr), Str("BoundValue"))
            env :+ (name, value.toString)
          } else { env }
        }
      }
      case v => error(s"Incorrect SubMap: $v")
    }

  final def getFullEnv(): List[List[(String, String)]] =
    st.heap.apply(NamedAddr("EXECUTION_STACK")) match {
      case (l: IRList) => {
        val globalObjAddr = findAddrInHeap(NamedAddr("REALM"), "GlobalObject")
        if (globalObjAddr >= 0L) {
          val subMapAddr = findAddrInHeap(DynamicAddr(globalObjAddr), "SubMap")
          val fullEnv = if (l.values.size != 0) {
            l.values.reverse.foldLeft(List[List[(String, String)]]()) {
              case (fullEnv, DynamicAddr(envAddr)) => {
                val env = ArrayBuffer[(String, String)]()
                val lexicalEnvAddr = st.heap.apply(DynamicAddr(envAddr), Str("LexicalEnvironment")) match {
                  case CompValue(_, DynamicAddr(addr), _) => addr
                  case v => -1L
                }
                if (lexicalEnvAddr >= 0L) {
                  val varNamesAddr = findAddrInHeap(DynamicAddr(lexicalEnvAddr), "VarNames")
                  if (varNamesAddr >= 0L) {
                    val varNames = st.heap.apply(DynamicAddr(varNamesAddr)) match {
                      case IRList(vars) => vars.toList
                      case v => error(s"Wrong case of VarNames: $v")
                    }
                    varNames.foreach((x) => {
                      val xName = x match {
                        case Str(n) => n
                        case v => error(s"Non-string VarName: $v")
                      }
                      val xAddr = findAddrInHeap(DynamicAddr(subMapAddr), xName)
                      val xValue = st.heap.apply(DynamicAddr(xAddr), Str("Value"))
                      env += ((xName, xValue.toString))
                    })
                  } else {
                    val insideSubMapAddr = findAddrInHeap(DynamicAddr(lexicalEnvAddr), "SubMap")
                    env.appendAll(getSubMapItems(DynamicAddr(insideSubMapAddr)))
                  }
                  fullEnv :+ env.toList
                } else { fullEnv }
              }
              case (fullEnv, _) => fullEnv
            }
          } else { List[List[(String, String)]]() }
          fullEnv
        } else { List[List[(String, String)]]() }
      }
      case v => {
        println("here")
        List[List[(String, String)]]()
      }
    }
}
