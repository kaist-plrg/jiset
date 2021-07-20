package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.ir.Parser._
import kr.ac.kaist.jiset.util.Useful._
import scala.Console._
import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex
import scala.scalajs.js.annotation._
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

@JSExportTopLevel("WebREPL")
@JSExportAll
class WebREPL(override val st: State) extends Debugger {
  val detail = true

  // print next target
  private def printNextTarget: Unit = println(s"[NEXT] ${st.context.name}: ${interp.nextTarget}")

  def execute(line: String): Unit = {
    line.split("\\s+").toList match {
      // help
      case CmdHelp.name :: _ =>
        Command.help

      // step
      case CmdStepOver.name :: _ =>
        stepOver
      case CmdStepOut.name :: _ =>
        stepOut
      case CmdStep.name :: _ | Nil | List("") =>
        interp.step

      // breakpoints
      case CmdBreak.name :: algoName :: _ =>
        addBreak(algoName)
      case CmdLsBreak.name :: _ =>
        breakpoints.zipWithIndex.foreach {
          case ((_, AlgoBreakPoint(name)), i) => println(f"$i: $name")
        }
      case CmdRmBreak.name :: opt :: _ =>
        rmBreak(opt)
      case CmdContinue.name :: _ =>
        continue

      // state info
      case CmdInfo.name :: algoName :: _ =>
        val algo = algos(algoName)
        println(algo.beautified)
      case CmdContext.name :: _ | CmdInfo.name :: Nil =>
        println(st.context.beautified)
      case CmdStack.name :: _ =>
        st.ctxtStack.reverse.zipWithIndex.reverse.foreach {
          case (context, i) => println(s"$i: ${context.name}")
        }

      // watch
      case CmdWatch.name :: exprStr :: _ =>
        addExpr(exprStr)
      case CmdLsWatch.name :: _ =>
        watchExprs.zipWithIndex.foreach {
          case (expr, i) => println(f"$i: ${expr.beautified}")
        }
      case CmdRmWatch.name :: opt :: _ =>
        rmWatch(opt)
      case CmdEvalWatch.name :: _ =>
        evalWatch

      case cmd :: _ =>
        println(s"The command `$cmd` does not exist. (Try `help`)")
    }

    printNextTarget
  }

  def getAlgo(opt: String): js.Array[String] = {
    st.context.algo match {
      case Some(algo) => opt match {
        case "id" => js.Array(algo.name)
        case "code" => algo.code.toJSArray
        case _ => js.Array()
      }
      case None => js.Array()
    }
  }
}
