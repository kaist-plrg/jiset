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
  private val breakpoints = ArrayBuffer[Regex]()

  // jline
  private val terminal: Terminal = TerminalBuilder.builder().build()
  private val completer: TreeCompleter =
    new TreeCompleter(Command.commands.map(x => node(x.name)): _*)
  private val reader: LineReader = LineReaderBuilder.builder()
    .terminal(terminal)
    .completer(completer)
    .build()
  private val prompt: String = LINE_SEP + s"${MAGENTA}analyzer>${RESET} "
  // check break point of control point
  private def isBreak(cp: ControlPoint): Boolean = cp match {
    case NodePoint(node: Entry, _) =>
      breakpoints.exists(x => optional(x.toString.toInt) match {
        case Some(uid) => node.uid == uid
        case None => x.matches(funcOf(node).name)
      })
    case NodePoint(node, _) => breakpoints.exists(x =>
      optional(x.toString.toInt) contains node.uid)
    case _ => false
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
          args.headOption match {
            case None => println("need arguments")
            case Some(bp) => breakpoints += bp.r
          }; true
        case CmdBreakList.name :: _ =>
          breakpoints.zipWithIndex.foreach {
            case (bp, i) => println(s"$i: $bp")
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
)

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

case object CmdBreak extends Command("break", "Add a break point.")

case object CmdBreakList extends Command("break-list", "Show the list of break points.")

case object CmdBreakRm extends Command("break-rm", "Remove a break point.")

case object CmdLog extends Command("log", "Dump the state.")

case object CmdGraph extends Command("graph", "Dump the current control graph.")

case object CmdExit extends Command("exit", "Exit the analysis.")

case object CmdStop extends Command("stop", "Stop the repl.")

case object CmdInfo extends Command("info", "Show abstract state of node")
