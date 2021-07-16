package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.ir.Parser._
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

// REPL
class REPL(override val st: State) extends Debugger {
  import interp._

  // continue
  var continue = false

  // stop
  private def stop(): Unit = { continue = true }

  // completer
  private val completer: TreeCompleter =
    new TreeCompleter(Command.commands.map(cmd => node(cmd.name)): _*)

  // jline
  private val terminal: Terminal = TerminalBuilder.builder().build()
  private val reader: LineReader = LineReaderBuilder.builder()
    .terminal(terminal)
    .completer(completer)
    .build()
  private val prompt: String = LINE_SEP + s"${MAGENTA}jiset>${RESET} "

  // print next target
  private def printNextTarget: Unit = println(s"[NEXT] ${st.context.name}: ${nextTarget}")

  def loop: Unit = {
    try while (reader.readLine(prompt) match {
      case null =>
        stop(); false
      case line => line.split("\\s+").toList match {
        // help
        case CmdHelp.name :: _ =>
          Command.help; true

        // step
        case CmdStepOver.name :: _ => ???
        case CmdStepOut.name :: _ => ???
        case CmdStep.name :: _ | Nil | List("") =>
          printNextTarget; step

        // breakpoints
        case CmdBreak.name :: _ => ???
        case CmdLsBreak.name :: _ => ???
        case CmdRmBreak.name :: _ => ???

        // state info
        case CmdInfo.name :: _ => ???
        case CmdContext.name :: _ => ???

        // watch
        case CmdWatch.name :: _ => ???
        case CmdLsWatch.name :: _ => ???
        case CmdAddWatch.name :: _ => ???
        case CmdRmWatch.name :: _ => ???

        case cmd :: _ =>
          println(s"The command `$cmd` does not exist. (Try `help`)")
          true
      }
    }) {}
    catch {
      case e: EndOfFileException => error("stop REPL")
    }
  }
}

object REPL {
  def apply(st: State) = {
    val repl = new REPL(st)
    repl.loop
  }
}

// command
private abstract class Command(
  val name: String,
  val info: String = ""
) { val options = List[String]() }

private object Command {
  val commands: List[Command] = List(
    CmdHelp,

    // step
    CmdStep,
    CmdStepOver,
    CmdStepOut,

    // breakpoints
    CmdBreak,
    CmdLsBreak,
    CmdRmBreak,

    // state info
    CmdInfo,
    CmdContext,

    // watch
    CmdWatch,
    CmdLsWatch,
    CmdAddWatch,
    CmdRmWatch,
  )
  val cmdMap: Map[String, Command] = commands.map(cmd => (cmd.name, cmd)).toMap

  def help = {
    println
    println("command list:")
    for (cmd <- commands) println("- %-25s%s".format(cmd.name, cmd.info))
  }
}

private case object CmdHelp extends Command("help")

// step
private case object CmdStep extends Command("step")
private case object CmdStepOver extends Command("step-over")
private case object CmdStepOut extends Command("step-out")

// breakpoints
private case object CmdBreak extends Command("break")
private case object CmdLsBreak extends Command("ls-break")
private case object CmdRmBreak extends Command("rm-break")

// state info
private case object CmdInfo extends Command("info")
private case object CmdContext extends Command("context")

// watch
private case object CmdWatch extends Command("watch")
private case object CmdLsWatch extends Command("ls-watch")
private case object CmdAddWatch extends Command("add-watch")
private case object CmdRmWatch extends Command("rm-watch")
