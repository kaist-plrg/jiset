package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer.command._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.spec.algorithm.SyntaxDirectedHead
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

// REPL for static analysis
case class REPL(sem: AbsSemantics) {
  // completer
  private val completer: TreeCompleter =
    new TreeCompleter(Command.commands.map(optionNode(_)): _*)
  private def optionNode(cmd: Command) = node(cmd.name :: (cmd match {
    case CmdGraph =>
      node(s"-${CmdGraph.total}") :: cfg.funcs.map(x => node(x.name))
    case _ => cmd.options.map(argNode(_))
  }): _*)
  private def argNode(opt: String) =
    node(s"-$opt" :: getArgNodes(opt): _*)
  private def getArgNodes(opt: String): List[TreeCompleter.Node] = opt match {
    case CmdBreak.func => cfg.funcs.map(x => node(x.name))
    case CmdBreak.block => (0 until cfg.nidGen.size).map(x => node(x.toString)).toList
    case CmdInfo.ret => cfg.funcs.map(x => node(x.name))
    case CmdInfo.block => (0 until cfg.nidGen.size).map(x => node(x.toString)).toList
    case _ => Nil
  }

  // jline
  private val terminal: Terminal = TerminalBuilder.builder().build()
  private val reader: LineReader = LineReaderBuilder.builder()
    .terminal(terminal)
    .completer(completer)
    .build()
  private val prompt: String = LINE_SEP + s"${MAGENTA}checker>${RESET} "

  // show help message at the first time
  lazy val firstHelp: Unit = { CmdHelp.showHelp; println }

  // run repl
  def apply(cpOpt: Option[ControlPoint]): Unit = cpOpt match {
    case Some(cp) if isContinue && !isBreak(cp) =>
    case _ => runDirect(cpOpt)
  }
  def runDirect(cp: Option[ControlPoint]): Unit = {
    firstHelp
    cp.map(cp => println(sem.getString(cp, CYAN, true)))
    try while ({
      reader.readLine(prompt) match {
        case null => stop
        case line => line.split("\\s+").toList match {
          case Nil | List("") => continue
          case name :: args => Command.cmdMap.get(name) match {
            case Some(cmd) => cmd(this, cp, args)
            case None =>
              println(s"The command `$name` does not exist. (Try `help`)")
          }
        }
      }
      !isContinue
    }) {}
    catch {
      case e: EndOfFileException => error("stop for debugging")
    }
  }

  // continue option
  def continue: Unit = isContinue = true
  private var isContinue: Boolean = false

  // break points
  val breakpoints = ArrayBuffer[(String, String)]()
  private def isBreak(cp: ControlPoint): Boolean = cp match {
    case NodePoint(node: Entry, _) => breakpoints.exists {
      case (CmdBreak.func, name) => name == cfg.funcOf(node).name
      case (CmdBreak.block, uid) => uid.toInt == node.uid
      case _ => false
    }
    case NodePoint(node, _) => breakpoints.exists {
      case (CmdBreak.block, uid) => uid.toInt == node.uid
      case _ => false
    }
    case _ => false
  }

  // stop
  def stop: Unit = { breakpoints.clear(); continue }
}
