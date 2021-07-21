package kr.ac.kaist.jiset.extractor.algorithm

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.extractor.algorithm.{ TokenParser, Compiler }
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec.algorithm.token.Token
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.JvmUseful._
import org.jline.builtins.Completers.TreeCompleter
import org.jline.builtins.Completers.TreeCompleter._
import org.jline.reader._
import org.jline.reader.impl._
import org.jline.terminal._
import org.jline.utils.InfoCmp.Capability
import org.jline.utils._
import org.jsoup.nodes.Document
import scala.Console.{ RESET, RED, YELLOW, GREEN, CYAN }

object CompileREPL {
  def run(version: String, secIds: Map[String, String])(
    implicit
    grammar: Grammar,
    document: Document
  ): Unit = {
    // jline
    val builder: TerminalBuilder = TerminalBuilder.builder()
    val terminal: Terminal = builder.build()
    def parseNode(cmd: String) = node(
      cmd,
      node("-insts"), node("-inst"), node("-expr"),
      node("-value"), node("-cond"), node("-ty"), node("-ref")
    )
    val completer: TreeCompleter = new TreeCompleter(
      parseNode(":raw"),
      parseNode(":token"),
      node(":quit"),
      node(":exit")
    )
    val reader: LineReader = LineReaderBuilder.builder()
      .terminal(terminal)
      .completer(completer)
      .build()
    val writer = terminal.writer()

    def stopMessage(msg: String): Unit = {
      println(msg)
      System.console().reader().read
    }

    def prompt: String = LINE_SEP + s"${CYAN}jiset>${RESET} "

    var keep: Boolean = true

    // get compile target
    val compileTarget = CompileTargets(version, secIds)
    import compileTarget._

    // get target
    def getTarget(words: List[String]): (CompileTarget, List[String]) = words match {
      case CompileTarget(target) :: rest => (target, rest)
      case _ => (InstsTarget, words)
    }

    def parse(raw: Boolean, words: List[String]): Unit = {
      // get parser
      val (target, input) = getTarget(words)

      // get code
      val code = (if (input.isEmpty) {
        // read multiple lines
        var list = List[String]()
        while (scala.io.StdIn.readLine match {
          case null | "" => false
          case str => list ::= str; true
        }) ()
        list.reverse
      } else List(input.mkString(" "))).map(unescapeHtml(_))

      val (tokens, result) = target.parse(code, raw)
      println(s"[Tokens] ${tokens.map(_.beautified).mkString(" ")}")
      if (result.successful) {
        val resultStr = result.get.beautified(index = true, asite = true)
        println(s"[Success] $resultStr")
      } else {
        println(s"[Failed] $result")
      }
    }

    while (keep) {
      // reader
      try {
        trimRight(reader.readLine(prompt)) match {
          case null =>
          case line => line.split("\\s+").toList match {
            case Nil | List("") =>
            case cmd :: rest if cmd.startsWith(":") => cmd.drop(1) match {
              case "exit" | "quit" => keep = false
              case "raw" => parse(true, rest)
              case "token" => parse(false, rest)
              case cmd => println(s"The command `$cmd` does not exist.")
            }
            case rest => parse(true, rest)
          }
        }
      } catch {
        case e: EndOfFileException => keep = false
        case e: UserInterruptException => keep = false
        case e: Throwable =>
          stopMessage(s"[Error] ${e.getStackTrace.mkString(LINE_SEP)}")
      }
    }
  }
}
