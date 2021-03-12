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
  private var breakpoints = ArrayBuffer[Regex]()

  // jline
  private val terminal: Terminal = TerminalBuilder.builder().build()
  private val completer: TreeCompleter = new TreeCompleter(
    node("log"),
    node("continue"),
    node("break"),
    node("break-list"),
    node("break-rm"),
    node("graph"),
    node("debug"),
    node("quit"),
    node("exit")
  )
  private val reader: LineReader = LineReaderBuilder.builder()
    .terminal(terminal)
    .completer(completer)
    .build()
  private val prompt: String = LINE_SEP + s"${MAGENTA}analyzer>${RESET} "
  // check break point of control point
  private def isBreak(cp: ControlPoint): Boolean = cp match {
    case NodePoint(entry: Entry, _) =>
      breakpoints.exists(_.matches(funcOf(entry).name))
    case _ => false
  }

  // quit
  private def quit(): Unit = { breakpoints.clear(); continue = true }

  // run repl
  def run(cp: ControlPoint): Unit = if (!continue || isBreak(cp)) {
    println(sem.getString(cp, CYAN, true))
    try while (reader.readLine(prompt) match {
      case null =>
        quit(); false
      case line => line.split("\\s+").toList match {
        case ("quit" | "exit") :: _ =>
          quit(); false
        case "log" :: _ =>
          sem.stat.dump(); true
        case "continue" :: _ =>
          continue = true; false
        case "debug" :: _ => error("stop for debugging")
        case "break" :: args =>
          args.headOption match {
            case None => ???
            case Some(bp) => breakpoints += bp.r
          }; true
        case "break-rm" :: args =>
          args.headOption match {
            case None => ???
            case Some(idx) => breakpoints.remove(idx.toInt)
          }; true
        case "break-list" :: _ =>
          breakpoints.zipWithIndex.foreach {
            case (bp, i) => println(s"$i: $bp")
          }; true
        case "graph" :: args =>
          val depth = optional(args.head.toInt)
          dumpCFG(sem, Some(cp), depth = depth); true
        case _ => continue = false; false
      }
    }) {}
    catch {
      case e: EndOfFileException => quit()
      case e: Throwable =>
    }
  }
}
