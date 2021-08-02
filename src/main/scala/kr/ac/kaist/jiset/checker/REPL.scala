package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.util.QueueWorklist
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
import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex

// REPL for type checking
object REPL {
  // breakpoints
  private var continue = false
  private val breakpoints = ArrayBuffer[(String, String)]()

  // completer
  private val completer: TreeCompleter =
    new TreeCompleter(Command.commands.map(optionNode(_)): _*)
  private def optionNode(cmd: Command) =
    node(cmd.name :: (cmd match {
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

  // help
  private lazy val help = { Command.help; println }

  // break
  private def break(args: List[String]) = {
    import CmdBreak._
    args match {
      case opt :: bp :: _ if options contains opt.substring(1) =>
        breakpoints += (opt.substring(1) -> bp)
      case _ => println("Inappropriate argument")
    }
  }

  // rm-break
  private def rmBreak(args: List[String]) = {
    import CmdRmBreak._;
    args match {
      case Nil => println("need arguments")
      case arg :: _ => optional(arg.toInt) match {
        case _ if arg == s"-$all" => breakpoints.clear
        case Some(idx) if idx.toInt < breakpoints.size =>
          breakpoints.remove(idx.toInt)
        case _ => println("Inappropriate argument")
      }
    }
  }

  // break heler
  private def isBreak(cp: ControlPoint): Boolean = cp match {
    case NodePoint(node: Entry, _) => breakpoints.exists {
      case (s"${ CmdBreak.func }", name) => name == cfg.funcOf(node).name
      case (s"${ CmdBreak.block }", uid) => uid.toInt == node.uid
      case _ => ???
    }
    case NodePoint(node, _) => breakpoints.exists {
      case (s"${ CmdBreak.block }", uid) => uid.toInt == node.uid
      case _ => false
    }
    case _ => false
  }

  // graph
  private def graph(cp: Option[ControlPoint], args: List[String]) = {
    import CmdGraph._
    optional(args.head.toInt) match {
      case Some(depth) => dumpCFG(cp, depth = Some(depth))
      case None if args.isEmpty => dumpCFG(cp, depth = Some(0))
      case None if args.head == s"-$total" => dumpCFG(cp, depth = None)
      case None => graphFunc(args.head, args.tail)
    }
  }
  private def graphFunc(
    fname: String,
    tail: List[String]
  ) = cfg.funcs.find(x => x.name == fname) match {
    case None => println("Inappropriate function name")
    case Some(func) =>
      if (func.complete) println("* complete function")
      else println("* incomplete function")
      val rpList = sem.getRpsForREPLByName(fname).toList.sortBy(_.view.toString)
      (optional(rpList(tail.head.toInt)), optional(tail.tail.head.toInt)) match {
        case (Some(rp), Some(depth)) => dumpCFG(Some(rp), depth = Some(depth))
        case (Some(rp), None) => dumpCFG(Some(rp), depth = Some(0))
        case (None, _) if tail.isEmpty =>
          dumpFunc(func)
          println
          println(s"View of function ${func.name}:")
          rpList.zipWithIndex.foreach {
            case (rp, i) => println(s"  $i: ${rp.view}")
          }
        case _ => println("Inappropriate argument")
      }
  }

  // stop
  private def stop(): Unit = { breakpoints.clear(); continue = true }

  // info
  private def info(args: List[String]) = {
    import CmdInfo._
    args match {
      case opt :: target :: _ if options contains opt.substring(1) =>
        printInfo(opt.substring(1), target)
      case _ => println("Inappropriate option")
    }
  }
  private def printInfo(opt: String, target: String): Unit = {
    import CmdInfo._
    val info = opt match {
      case CmdInfo.ret => sem.getReturnPointByName(target)
      case CmdInfo.block if optional(target.toInt) != None =>
        sem.getNodePointsById(target.toInt)
      case _ => println("Inappropriate argument"); Set()
    }
    info.foreach(cp => {
      println(sem.getString(cp, CYAN, true))
      println
    })
  }

  // entry
  private def entry(cp: ControlPoint, args: List[String]): Unit = {
    import CmdEntry._
    var paths = Map[ControlPoint, Path]()
    var visited = Set[ControlPoint]()
    val worklist = new QueueWorklist[Path](List(Nil))

    @tailrec
    def aux: Unit = worklist.next match {
      case Some(path) => {
        val curCp = path.headOption.getOrElse(cp)
        val func = sem.funcOf(curCp)
        val view = curCp.view
        val rp = ReturnPoint(func, view)
        visited += curCp
        func.headOption match {
          case Some(head: SyntaxDirectedHead) if head.withParams.isEmpty =>
            if (!(paths contains curCp)) paths += curCp -> path.reverse
          case _ => for {
            (nextCp, _) <- sem.getRetEdges(rp)
            if !(visited contains nextCp)
          } {
            worklist += nextCp :: path
          }
        }
        aux
      }
      case _ =>
    }
    aux

    // get only names

    // options
    args match {
      case "-path" :: _ => for {
        (_, path) <- paths
        len = path.length
        _ = println(s"[LENGTH = $len]")
        np <- path
        func = sem.funcOf(np)
        name = func.name
      } println(s"   <- $name:$np")
      case "-graph" :: _ => {
        val (topCp, shortest) = paths.toList.minBy(_._2.length)
        val func = sem.funcOf(topCp)
        val name = func.name
        println(s"- entry with the shortest path: $name:$topCp")
        dumpCFG(Some(cp), path = Some(shortest))
      }
      case _ => for {
        (topCp, _) <- paths
        f = sem.funcOf(topCp)
        name = f.name
      } println(name)
    }
  }

  def astList(args: List[String]) = args match {
    case name :: prop :: _ => getAstList(name, prop)
    case _ => println("need two argument")
  }
  def getAstList(name: String, prop: String) = (name, prop) match {
    case ("IdentifierName", "StringValue") =>
      println("It just returns StrT")
    case ("NumericLiteral", "NumericValue") =>
      println("It just returns NumT")
    case ("StringLiteral", "StringValue" | "SV") =>
      println("It just returns StrT")
    case (_, "TV" | "TRV") =>
      println("It just returns StrT")
    case (_, "MV") =>
      println("It just returns NumT")
    case (_, prop) if cfg.spec.grammar.nameMap contains prop => AstT(prop)
    case _ => optional(cfg.getSyntaxFids(name, prop).toList) match {
      case None if prop == "Contains" =>
        println("It just returns BoolT")
      case None =>
        println(s"$name.$prop does not exist")
      case Some(fids) =>
        fids.foreach(fid => {
          val func = cfg.fidMap(fid)
          func.headOption match {
            case Some(head: SyntaxDirectedHead) => println(func.name)
            case _ =>
          }
        })
    }
  }

  // run repl
  def run(cp: ControlPoint): Unit = if (!continue || isBreak(cp)) runDirect(cp)
  def runDirect(givenCP: ControlPoint): Unit = {
    val cp = if (givenCP == null) None else Some(givenCP)
    help
    cp.map(cp => println(sem.getString(cp, CYAN, true)))
    try while (reader.readLine(prompt) match {
      case null =>
        stop(); false
      case line => line.split("\\s+").toList match {
        case CmdHelp.name :: _ =>
          Command.help; true
        case CmdContinue.name :: _ =>
          continue = true; false
        case CmdBreak.name :: args =>
          break(args); true
        case CmdListBreak.name :: _ =>
          breakpoints.zipWithIndex.foreach {
            case ((k, v), i) => println(f"$i: $k%-15s $v")
          }; true
        case CmdRmBreak.name :: args =>
          rmBreak(args); true
        case CmdLog.name :: _ =>
          Stat.dump(); true
        case CmdGraph.name :: args =>
          graph(cp, args); true
        case CmdExit.name :: _ => error("stop for debugging")
        case CmdStop.name :: _ =>
          stop(); false
        case CmdInfo.name :: args =>
          info(args); true
        case CmdEntry.name :: args =>
          cp.map(entry(_, args)); true
        case CmdWorklist.name :: args =>
          worklist.foreach(println(_)); true
        case CmdAstList.name :: args =>
          astList(args); true
        case Nil | List("") =>
          continue = false; false
        case cmd :: _ =>
          println(s"The command `$cmd` does not exist. (Try `help`)")
          true
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
) { val options = List[String]() }

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
    CmdAstList,
  )
  val cmdMap: Map[String, Command] = commands.map(cmd => (cmd.name, cmd)).toMap

  def help = {
    println
    println("command list:")
    for (cmd <- commands) println("- %-25s%s".format(cmd.name, cmd.info))
  }
}

private case object CmdHelp extends Command("help")

private case object CmdContinue extends Command("continue", "Continue type checking.")

private case object CmdBreak extends Command("break", "Add a break point.") {
  val func = "func"
  val block = "block"
  override val options = List(func, block)
}

private case object CmdListBreak extends Command("list-break", "Show the list of break points.")

private case object CmdRmBreak extends Command("rm-break", "Remove a break point.") {
  val all = "all"
  override val options = List(all)
}

private case object CmdLog extends Command("log", "Dump the state.")

private case object CmdGraph extends Command("graph", "Dump the current control graph.") {
  val total = "total"
}

private case object CmdExit extends Command("exit", "Exit the type checking.")

private case object CmdStop extends Command("stop", "Stop the repl.")

private case object CmdInfo extends Command("info", "Show abstract state of node") {
  val ret = "ret"
  val block = "block"
  override val options = List(ret, block)
}

private case object CmdEntry extends Command("entry", "Show the set of entry functions of current function") {
  val path = "path"
  val graph = "graph"
  override val options = List(path, graph)
}

private case object CmdWorklist extends Command("worklist", "Show all the control points in the worklist")

private case object CmdAstList extends Command("ast-list", "Show the AST list of given name and property")
