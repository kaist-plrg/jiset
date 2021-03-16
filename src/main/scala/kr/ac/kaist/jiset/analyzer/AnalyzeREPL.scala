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
  lazy val help = { Command.help; println }

  // info
  private def info(arg: Option[String] = None): Unit = arg match {
    case None => println(s"need arguments")
    case Some(arg) => optional(arg.toInt) match {
      // return point
      case None => sem(arg).foreach(rp => {
        println(sem.getString(rp, CYAN, true))
        println
      })
      // uid
      case Some(uid) => sem(uid).foreach(np => {
        println(sem.getString(np, CYAN, true))
        println
      })
    }
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
            case Some(idx) => breakpoints.remove(idx.toInt)
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
          // TODO handle options
          info(optional(args.head)); true
        case _ => continue = false; false
      }
    }) {}
    catch {
      case e: EndOfFileException => error("stop for debugging")
    }
  }
}

// command
abstract class Command(
  val name: String,
  val info: String = ""
) { val options = List[CmdOption]() }

// option
abstract class CmdOption(val name: String)

object Command {
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
  )
  val cmdMap: Map[String, Command] = commands.map(cmd => (cmd.name, cmd)).toMap

  def help = {
    println
    println("command list:")
    for (cmd <- commands) println("- %-25s%s".format(cmd.name, cmd.info))
  }
}

case object CmdHelp extends Command("help")

case object CmdContinue extends Command("continue", "Continue the analysis.")

case object CmdBreak extends Command("break", "Add a break point.") {

  case object FuncTarget extends CmdOption("func")

  case object BlockTarget extends CmdOption("block")

  override val options = List(FuncTarget, BlockTarget)
}

case object CmdBreakList extends Command("break-list", "Show the list of break points.")

case object CmdBreakRm extends Command("break-rm", "Remove a break point.")

case object CmdLog extends Command("log", "Dump the state.")

case object CmdGraph extends Command("graph", "Dump the current control graph.")

case object CmdExit extends Command("exit", "Exit the analysis.")

case object CmdStop extends Command("stop", "Stop the repl.")

case object CmdInfo extends Command("info", "Show abstract state of node")
