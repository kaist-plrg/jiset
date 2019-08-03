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
    val completer: TreeCompleter = new TreeCompleter(
      node("all"),
      node("get-first"),
      node("filter")
    )
    val reader: LineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .completer(completer)
      .build()
    val writer = terminal.writer()

    def stopMessage(msg: String): Unit = {
      print(msg)
      System.console().reader().read
    }

    implicit def tokenListOrdering[Token]: Ordering[List[Token]] = new Ordering[List[Token]] {
      implicit def compare(x: List[Token], y: List[Token]): Int = (x, y) match {
        case (Nil, Nil) => 0
        case (Nil, _) => -1
        case (_, Nil) => 1
        case (xh :: xt, yh :: yt) =>
          val xstr = xh.toString
          val ystr = yh.toString
          if (xstr == ystr) compare(xt, yt)
          else xstr.compare(ystr)
      }
    }

    val tokenLists: List[List[Token]] = (for {
      file <- shuffle(walkTree(new File(algoDir))).toList
      filename = file.getName
      if jsonFilter(filename)
      algo = Algorithm(file.toString)
      step <- algo.getSteps(Nil)
    } yield (List[Token]() /: step.tokens) {
      case (l, StepList(_)) => Out :: In :: l
      case (l, x) => x :: l
    }.reverse).sorted

    def prompt: String = CYAN + "repl-algo> " + RESET

    def show(list: List[List[Token]]): Unit = {
      list.foreach(l => println(l.mkString(" ")))
      printlnGreen(s"total: ${list.length}")
    }

    type Filter = (List[Token], List[String]) => Boolean

    def checkSub: Filter = (_, _) match {
      case (_, Nil) => true
      case (Nil, _) => false
      case (t :: ttl, ss @ s :: stl) =>
        if (t.toString == s) checkSub(ttl, stl)
        else checkSub(ttl, ss)
    }

    var keep: Boolean = true
    while (keep) try {
      reader.readLine(prompt) match {
        case null =>
        case str => str.split("\\s+").toList match {
          case Nil =>
          case cmd :: args => cmd match {
            // show all steps
            case "all" => show(tokenLists)

            // print statistics of first tokens
            case "get-first" => (Map[Token, Int]() /: tokenLists.map(_.head)) {
              case (m, t) => m.get(t) match {
                case Some(k) => m + (t -> (k + 1))
                case None => m + (t -> 1)
              }
            }.toSeq.sortBy(_._2).reverse foreach {
              case (t, k) => println(s"$t -> $k")
            }

            // filtering
            case "filter" => args match {
              // no filter
              case Nil => printlnRed(s"no filter for `filter` command")
              case fname :: args => ((fname match {
                // prefix filter
                case "pre" => Left(_.map(_.toString) startsWith _)
                // sub-sequence filter
                case "sub" => Left(checkSub)
                // unknown filter
                case f => Right(f)
              }): Either[Filter, String]) match {
                case Left(filter) =>
                  show(tokenLists.filter(ts => filter(ts, args)))
                case Right(f) =>
                  printlnRed(s"unknown filter for `filter` command: $f")
              }
            }

            // exit repl
            case "exit" => keep = false

            // unknown commands
            case t => printlnRed(s"unknown commands: $t")
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
