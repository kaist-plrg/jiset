package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.spec.algorithm.SyntaxDirectedHead
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
object AnalyzeREPL {
  val sem = AbsSemantics

  // breakpoints
  private var continue = false
  private val breakpoints = ArrayBuffer[(CmdOption, Regex)]()

  // completer
  private val completer: TreeCompleter =
    new TreeCompleter(Command.commands.map(optionNode(_)): _*)
  private def optionNode(cmd: Command) =
    node(cmd.name :: (cmd match {
      case CmdGraph => cfg.funcs.map(x => node(x.name))
      case _ => cmd.options.map(argNode(_))
    }): _*)
  private def argNode(opt: CmdOption) =
    node(s"-${opt.name}" :: getArgNodes(opt): _*)
  private def getArgNodes(opt: CmdOption): List[TreeCompleter.Node] = opt match {
    case CmdBreak.FuncTarget => cfg.funcs.map(x => node(x.name))
    case CmdBreak.BlockTarget => (0 until cfg.nidGen.size).map(x => node(x.toString)).toList
    case CmdInfo.RetTarget => cfg.funcs.map(x => node(x.name))
    case CmdInfo.BlockTarget => (0 until cfg.nidGen.size).map(x => node(x.toString)).toList
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
      case (CmdBreak.FuncTarget, name) => name.matches(cfg.funcOf(node).name)
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
        case CmdInfo.RetTarget => sem.getReturnPointByName(str)
        case CmdInfo.BlockTarget => sem.getNodePointsById(str.toInt)
      }
      info.foreach(cp => {
        println(sem.getString(cp, CYAN, true))
        println
      })
  }

  // entry
  var visited: Set[ReturnPoint] = Set()

  private def getEntryFunc(cp: ControlPoint): Set[String] = {
    val rp = getRpOf(cp)
    if (visited contains rp) Set()
    else {
      visited += rp
      rp.func.algo.head match {
        case head @ SyntaxDirectedHead(_, _, _, _, _, withParam) if withParam.isEmpty =>
          Set(rp.func.name)
        case _ => getCallNodes(rp).flatMap(getEntryFunc(_))
      }
    }
  }

  private def getRpOf(cp: ControlPoint): ReturnPoint = cp match {
    case rp @ ReturnPoint(_, _) => rp
    case NodePoint(node, view) =>
      cfg.funcs.find(x => x.nodes.exists(x => x.uid == node.uid)) match {
        case None => ???
        case Some(f) => ReturnPoint(f, view)
      }
  }

  private def getCallNodes(rp: ReturnPoint): Set[NodePoint[Call]] =
    sem.getRetEdges(rp).map(_._1)

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
        case CmdListBreak.name :: _ =>
          breakpoints.zipWithIndex.foreach {
            case (bp, i) => println("%s: %-15s %s".format(i, bp._1, bp._2))
          }; true
        case CmdRmBreak.name :: args =>
          args.headOption match {
            case None => println("need arguments")
            case Some(idx) if idx.toInt < breakpoints.size => breakpoints.remove(idx.toInt)
            case Some(idx) => println(s"out of index: $idx")
          }; true
        case CmdLog.name :: _ =>
          Stat.dump(); true
        case CmdGraph.name :: args =>
          optional(args.head.toInt) match {
            case depth @ Some(_) => dumpCFG(Some(cp), depth = depth)
            case None =>
              val func = cfg.funcs.find(x => args contains x.name)
              val targetCp = func match {
                case Some(func) => ReturnPoint(func, View(List()))
                case None => cp
              }
              dumpCFG(Some(targetCp), depth = Some(0))
          }
          true
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
        case CmdEntry.name :: _ =>
          visited = Set()
          getEntryFunc(cp).foreach(println(_))
          true
        case CmdWorklist.name :: args =>
          worklist.foreach(println(_))
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
    CmdListBreak,
    CmdRmBreak,
    CmdLog,
    CmdGraph,
    CmdExit,
    CmdStop,
    CmdInfo,
    CmdEntry,
    CmdWorklist,
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

private case object CmdListBreak extends Command("list-break", "Show the list of break points.")

private case object CmdRmBreak extends Command("rm-break", "Remove a break point.")

private case object CmdLog extends Command("log", "Dump the state.")

private case object CmdGraph extends Command("graph", "Dump the current control graph.")

private case object CmdExit extends Command("exit", "Exit the analysis.")

private case object CmdStop extends Command("stop", "Stop the repl.")

private case object CmdInfo extends Command("info", "Show abstract state of node") {

  case object RetTarget extends CmdOption("ret")

  case object BlockTarget extends CmdOption("block")

  override val options = List(RetTarget, BlockTarget)
}

private case object CmdEntry extends Command("entry", "Show the set of entry functions of current function")

private case object CmdWorklist extends Command("worklist", "Show all the control points in the worklist")
