package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.analyzer
import kr.ac.kaist.jiset.util.Useful._
import org.jline.builtins.Completers.TreeCompleter
import org.jline.builtins.Completers.TreeCompleter.{ Node => CNode, node }
import org.jline.reader._
import org.jline.reader.impl._
import org.jline.terminal._
import org.jline.utils.InfoCmp.Capability
import org.jline.utils._
import scala.Console._
import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex

// analyze repl
class AnalyzeREPL(sem: AbsSemantics) {
  import sem.cfg._

  // breakpoints
  private var continue = false
  private val breakpoints = ArrayBuffer[(CmdOption, Regex)]()

  // completer
  private val completer: TreeCompleter =
    new TreeCompleter(Command.commands.map(optionNode(_)): _*)
  private def optionNode(cmd: Command) =
    node(cmd.name :: cmd.options.map(argNode(_)): _*)
  private def argNode(opt: CmdOption) =
    node(s"-${opt.name}" :: getArgNodes(opt): _*)
  private def getArgNodes(opt: CmdOption): List[TreeCompleter.Node] = opt match {
    case CmdBreak.FuncTarget => funcs.map(x => node(x.name))
    case CmdBreak.BlockTarget => (0 until nidGen.size).map(x => node(x.toString)).toList
    case CmdInfo.RetTarget => funcs.map(x => node(x.name))
    case CmdInfo.BlockTarget => (0 until nidGen.size).map(x => node(x.toString)).toList
    case _ => Nil
  }

  // jline
  private val terminal: Terminal = TerminalBuilder.builder().build()
  private val reader: LineReader = LineReaderBuilder.builder()
    .terminal(terminal)
    .completer(completer)
    .build()
  private val prompt: String = LINE_SEP + s"${MAGENTA}analyzer>${RESET} "

  // helper for break
  private def isBreak(cp: ControlPoint): Boolean = cp match {
    case NodePoint(node: Entry, _) => breakpoints.exists {
      case (CmdBreak.FuncTarget, name) => name.matches(funcOf(node).name)
      case (CmdBreak.BlockTarget, uid) => uid.toString.toInt == node.uid
      case _ => ???
    }
    case NodePoint(node, _) => breakpoints.exists {
      case (CmdBreak.BlockTarget, uid) => uid.toString.toInt == node.uid
      case _ => false
    }
    case _ => false
  }
  private def addBreak(opt: CmdOption, bp: List[String]): Unit = bp match {
    case Nil => println("need arguments")
    case str :: _ => breakpoints += (opt -> str.r)
  }

  // stop
  private def stop(): Unit = { breakpoints.clear(); continue = true }

  // help
  private lazy val help = { Command.help; println }

  // info
  private def printInfo(opt: CmdOption, args: List[String]): Unit = args match {
    case Nil => println("need arguments")
    case str :: _ =>
      val info = opt match {
        case CmdInfo.RetTarget => sem(str)
        case CmdInfo.BlockTarget => sem(str.toInt)
      }
      info.foreach(cp => {
        println(sem.getString(cp, CYAN, true))
        println
      })
  }

  private def getFuncName(args: List[String]): String = args.lastIndexOf("@") match {
    case n if n != -1 && n < args.size - 1 =>
      val funcInfo = args(n + 1)
      funcInfo.slice(0, funcInfo.indexOf(":"))
    case _ => ""
  }

  // run repl
  def run(cp: ControlPoint): Unit = if (!continue || isBreak(cp)) {
    help
    println(sem.getString(cp, CYAN, true))
    try while (reader.readLine(prompt) match {
      case null =>
        stop(); false
      case line => line.split("\\s+").toList match {
        case CmdHelp.name :: _ =>
          Command.help; true
        case CmdContinue.name :: _ =>
          continue = true; false
        case CmdBreak.name :: args =>
          import CmdBreak._
          args match {
            case s"-${ FuncTarget.name }" :: bp => addBreak(FuncTarget, bp)
            case s"-${ BlockTarget.name }" :: bp => addBreak(BlockTarget, bp)
            case _ => println("Inappropriate option")
          }; true
        case CmdBreakList.name :: _ =>
          breakpoints.zipWithIndex.foreach {
            case (bp, i) => println("%s: %-15s %s".format(i, bp._1, bp._2))
          }; true
        case CmdBreakRm.name :: args =>
          args.headOption match {
            case None => println("need arguments")
            case Some(idx) if idx.toInt < breakpoints.size => breakpoints.remove(idx.toInt)
            case Some(idx) => println(s"out of index: $idx")
          }; true
        case CmdLog.name :: _ =>
          sem.stat.dump(); true
        case CmdGraph.name :: args =>
          val depth = optional(args.head.toInt)
          dumpCFG(sem, Some(cp), depth = depth); true
        case CmdExit.name :: _ => error("stop for debugging")
        case CmdStop.name :: _ =>
          stop(); false
        case CmdInfo.name :: args =>
          import CmdInfo._
          args match {
            case s"-${ RetTarget.name }" :: bp => printInfo(RetTarget, bp)
            case s"-${ BlockTarget.name }" :: bp => printInfo(BlockTarget, bp)
            case _ => println("Inappropriate option")
          }; true
        case CmdBug.name :: args =>
          getFuncName(args) match {
            case "" => println("Inappropriate argument")
            case funcName =>
              println(s"$funcName")
              breakpoints += CmdBreak.FuncTarget -> funcName.r
          }
          true
        case _ => continue = false; false
      }
    }) {}
    catch {
      case e: EndOfFileException => error("stop for debugging")
    }
  }
}

// command
private abstract class Command(
  val name: String,
  val info: String = ""
) { val options = List[CmdOption]() }

// option
private abstract class CmdOption(val name: String)

private object Command {
  val commands: List[Command] = List(
    CmdHelp,
    CmdContinue,
    CmdBreak,
    CmdBreakList,
    CmdBreakRm,
    CmdLog,
    CmdGraph,
    CmdExit,
    CmdStop,
    CmdInfo,
    CmdBug,
  )
  val cmdMap: Map[String, Command] = commands.map(cmd => (cmd.name, cmd)).toMap

  def help = {
    println
    println("command list:")
    for (cmd <- commands) println("- %-25s%s".format(cmd.name, cmd.info))
  }
}

private case object CmdHelp extends Command("help")

private case object CmdContinue extends Command("continue", "Continue the analysis.")

private case object CmdBreak extends Command("break", "Add a break point.") {

  case object FuncTarget extends CmdOption("func")

  case object BlockTarget extends CmdOption("block")

  override val options = List(FuncTarget, BlockTarget)
}

private case object CmdBreakList extends Command("break-list", "Show the list of break points.")

private case object CmdBreakRm extends Command("break-rm", "Remove a break point.")

private case object CmdLog extends Command("log", "Dump the state.")

private case object CmdGraph extends Command("graph", "Dump the current control graph.")

private case object CmdExit extends Command("exit", "Exit the analysis.")

private case object CmdStop extends Command("stop", "Stop the repl.")

private case object CmdInfo extends Command("info", "Show abstract state of node") {

  case object RetTarget extends CmdOption("ret")

  case object BlockTarget extends CmdOption("block")

  override val options = List(RetTarget, BlockTarget)
}

private case object CmdBug extends Command("[Bug]", "Reproduce the bug")
