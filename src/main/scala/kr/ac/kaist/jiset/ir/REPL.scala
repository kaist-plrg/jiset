package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.js._
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
  // set detail
  val detail = true

  // completer
  private val completer: TreeCompleter =
    new TreeCompleter(Command.commands.map(optionNode(_)): _*)
  private def optionNode(cmd: Command) =
    node(cmd.name :: cmd.options.map(node(_)): _*)

  // jline
  private val terminal: Terminal = TerminalBuilder.builder().build()
  private val reader: LineReader = LineReaderBuilder.builder()
    .terminal(terminal)
    .completer(completer)
    .build()
  private val prompt: String = LINE_SEP + s"${MAGENTA}jiset>${RESET} "

  // print next target
  private def printNextTarget: Unit = println(s"[NEXT] ${st.context.name}: ${interp.nextTarget}")

  def loop: Unit = {
    try while ({
      printNextTarget; reader.readLine(prompt) match {
        case null => false
        case line => line.split("\\s+").toList match {
          // help
          case CmdHelp.name :: _ =>
            Command.help; true

          // step
          case CmdStepOver.name :: _ =>
            stepOver; true
          case CmdStepOut.name :: _ =>
            stepOut; true
          case CmdStep.name :: _ | Nil | List("") =>
            interp.step

          // breakpoints
          case CmdBreak.name :: algoName :: _ =>
            addBreak(algoName); true
          case CmdLsBreak.name :: _ =>
            breakpoints.zipWithIndex.foreach {
              case ((_, AlgoBreakPoint(name)), i) => println(f"$i: $name")
            }; true
          case CmdRmBreak.name :: opt :: _ =>
            rmBreak(opt); true
          case CmdContinue.name :: _ =>
            continue; true

          // state info
          case CmdInfo.name :: algoName :: _ =>
            val algo = algos(algoName)
            println(algo.beautified); true
          case CmdContext.name :: _ | CmdInfo.name :: Nil =>
            println(st.context.beautified); true
          case CmdStack.name :: _ =>
            st.ctxtStack.reverse.zipWithIndex.reverse.foreach {
              case (context, i) => println(s"$i: ${context.name}")
            }; true

          // watch
          case CmdWatch.name :: exprStr :: _ =>
            addExpr(exprStr); true
          case CmdLsWatch.name :: _ =>
            watchExprs.zipWithIndex.foreach {
              case (expr, i) => println(f"$i: ${expr.beautified}")
            }; true
          case CmdRmWatch.name :: opt :: _ =>
            rmWatch(opt); true
          case CmdEvalWatch.name :: _ =>
            evalWatch; true

          case cmd :: _ =>
            println(s"The command `$cmd` does not exist. (Try `help`)")
            true
        }
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
  val info: String = "",
  val options: List[String] = List()
)

private object Command {
  val algoNames = algos.keySet.toList.sorted
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
    CmdContinue,

    // state info
    CmdInfo,
    CmdContext,
    CmdStack,

    // watch
    CmdWatch,
    CmdLsWatch,
    CmdEvalWatch,
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
private case object CmdStep extends Command(
  "step",
  "interp one instruction"
)
private case object CmdStepOver extends Command(
  "step-over",
  "interp until next instruction in same context"
)
private case object CmdStepOut extends Command(
  "step-out",
  "interp unitl current algorithm is terminated"
)

// breakpoints
private case object CmdBreak extends Command(
  "break",
  "add a breakpoint with given algorithm name",
  Command.algoNames
)
private case object CmdLsBreak extends Command(
  "ls-break",
  "list breakpoints"
)
private case object CmdRmBreak extends Command(
  "rm-break",
  "remove a breakpoint by given index"
)
private case object CmdContinue extends Command(
  "continue",
  "interp until reaching breakpoints"
)

// state info
private case object CmdInfo extends Command(
  "info",
  "show algorithm information",
  Command.algoNames
)
private case object CmdContext extends Command(
  "context",
  "show current context information"
)
private case object CmdStack extends Command(
  "stack",
  "show current context stack information"
)

// watch
private case object CmdWatch extends Command(
  "watch",
  "add a watch expression"
)
private case object CmdLsWatch extends Command(
  "ls-watch",
  "list watch expressions"
)
private case object CmdEvalWatch extends Command(
  "eval-watch",
  "evaluate watch expressions and show results"
)
private case object CmdRmWatch extends Command(
  "rm-watch",
  "remove a watch expression by given index"
)
