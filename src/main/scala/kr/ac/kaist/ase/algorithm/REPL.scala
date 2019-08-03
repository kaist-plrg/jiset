package kr.ac.kaist.ase.algorithm

import java.io.File
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.{ LINE_SEP, RESOURCE_DIR, VERSION }
import org.jline.builtins.Completers.TreeCompleter
import org.jline.reader._
import org.jline.reader.impl._
import org.jline.terminal._
import org.jline.utils.InfoCmp.Capability
import org.jline.utils._
import scala.Console.{ RESET, CYAN }
import scala.util.Random.shuffle
import scala.util.{ Try, Success, Failure }

import TreeCompleter._

// REPL
object REPL {
  // algorithm files
  val algoDir = s"$RESOURCE_DIR/$VERSION/auto/algorithm"

  def run: Unit = {
    val builder: TerminalBuilder = TerminalBuilder.builder()
    val terminal: Terminal = builder.build()
    val completer: TreeCompleter = new TreeCompleter(node("get-first"))
    val reader: LineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .completer(completer)
      .build()
    val writer = terminal.writer()

    def stopMessage(msg: String): Unit = {
      print(msg)
      System.console().reader().read
    }
    val algos: List[Algorithm] = for {
      file <- shuffle(walkTree(new File(algoDir))).toList
      filename = file.getName
      if jsonFilter(filename)
    } yield Algorithm(file.toString)

    val tokenLists: List[List[Token]] = for {
      algo <- algos
      step <- algo.getSteps(Nil)
    } yield (List[Token]() /: step.tokens) {
      case (l, StepList(_)) => Out :: In :: l
      case (l, x) => x :: l
    }.reverse

    def prompt: String = CYAN + "repl-algo> " + RESET

    var keep: Boolean = true
    while (keep) try {
      reader.readLine(prompt) match {
        case null =>
        case str => str.split("\\s+").toList match {
          case Nil =>
          case cmd :: args => cmd match {
            // print statistics of first tokens
            case "get-first" =>
              val stat = (Map[Token, Int]() /: tokenLists.map(_.head)) {
                case (m, t) => m.get(t) match {
                  case Some(k) => m + (t -> (k + 1))
                  case None => m + (t -> 1)
                }
              }
              stat.toSeq.sortBy(_._2).reverse foreach {
                case (t, k) => println(s"$t -> $k")
              }

            // exit repl
            case "exit" => keep = false

            // unknown commands
            case t => println(s"unknown commands: $t")
          }
        }
      }
    } catch {
      case e: EndOfFileException => keep = false
      case e: UserInterruptException => keep = false
      case e: java.lang.RuntimeException => stopMessage(s"")
      case e: Throwable => stopMessage(s"ERROR: $e")
    }
  }
}
