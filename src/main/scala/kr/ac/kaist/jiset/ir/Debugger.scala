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
  val breakpoints = ArrayBuffer[(InterpHook, BreakPoint)]()

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
    breakpoints += ((hook, bp))
  }

  // get breakpoint by index
  private def getBreakIdx(idx: String): Int = optional(idx.toInt) match {
    case Some(idx) if idx < breakpoints.size => idx
    case None => error("wrong breakpoints index: $idx")
  }

  // remove break
  final def rmBreak(opt: String) = opt match {
    case "all" =>
      breakpoints.foreach { case (hook, _) => interp.unsubscribe(hook) }
      breakpoints.clear
    case _ =>
      val idx = getBreakIdx(opt)
      val (hook, _) = breakpoints(idx)
      breakpoints.remove(idx)
      interp.unsubscribe(hook)
  }
  // toggle break
  final def toggleBreak(opt: String) = opt match {
    case "all" =>
      breakpoints.foreach { case (_, bp) => bp.toggle() }
    case _ =>
      val idx = getBreakIdx(opt)
      val (_, bp) = breakpoints(idx)
      bp.toggle()
  }

  // check if current step is in break
  final def isBreak: Boolean = breakpoints.foldLeft(false) {
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
      case Absent => -1L
      case v => error(s"Wrong with finding address in heap: $v")
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

  final def getFullEnv(): List[(String, String)] =
    st.heap.apply(NamedAddr("EXECUTION_STACK")) match {
      case (l: IRList) => {
        val env = ArrayBuffer[(String, String)]()
        if (l.values.size != 0) {
          val varNames = l.values.reverse.foldLeft(List[PureValue]()) {
            case (varNames, DynamicAddr(envAddr)) => {
              val lexicalEnvAddr = st.heap.apply(DynamicAddr(envAddr), Str("LexicalEnvironment")) match {
                case CompValue(_, DynamicAddr(addr), _) => addr
                case v => -1L
              }
              if (lexicalEnvAddr >= 0L) {
                val varNamesAddr = findAddrInHeap(DynamicAddr(lexicalEnvAddr), "VarNames")
                if (varNamesAddr >= 0L) {
                  st.heap.apply(DynamicAddr(varNamesAddr)) match {
                    case IRList(vars) => varNames ::: vars.toList
                    case v => error(s"Wrong case of VarNames: $v")
                  }
                } else {
                  val insideSubMapAddr = findAddrInHeap(DynamicAddr(lexicalEnvAddr), "SubMap")
                  env.appendAll(getSubMapItems(DynamicAddr(insideSubMapAddr)))
                  varNames
                }
              } else { varNames }
            }
            case (varNames, _) => varNames
          }
          val globalObjAddr = findAddrInHeap(NamedAddr("REALM"), "GlobalObject")
          val subMapAddr = findAddrInHeap(DynamicAddr(globalObjAddr), "SubMap")
          varNames.foreach((x) => {
            val xName = x match {
              case Str(n) => n
              case v => error(s"Non-string VarName: $v")
            }
            val xAddr = findAddrInHeap(DynamicAddr(subMapAddr), xName)
            val xValue = st.heap.apply(DynamicAddr(xAddr), Str("Value"))
            env += ((xName, xValue.toString))
          })
        }
        env.toList
      }
      case v => error(s"Wrong case of Execution_Stack: $v")
    }
}
