package kr.ac.kaist.jiset.parser.algorithm

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.parser.algorithm.{ TokenParser, Compiler }
import kr.ac.kaist.jiset.spec.JsonProtocol._
import kr.ac.kaist.jiset.spec.algorithm.Token
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.util.Useful._
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
  def run(implicit grammar: Grammar, document: Document): Unit = {
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

    // get parser
    type Parser = Compiler.Parser[IRNode]
    def getParser(words: List[String]): (Parser, Boolean, List[String]) = {
      import Compiler._
      words match {
        case "-insts" :: rest => (normalizedStmts, true, rest)
        case "-inst" :: rest => (stmt, true, rest)
        case "-expr" :: rest => (expr2inst(expr), false, rest)
        case "-value" :: rest => (valueParser, false, rest)
        case "-cond" :: rest => (expr2inst(cond), false, rest)
        case "-ty" :: rest => (ty, false, rest)
        case "-ref" :: rest => (ref2inst(ref), false, rest)
        case rest => (normalizedStmts, true, rest)
      }
    }

    def parse(raw: Boolean, words: List[String]): Unit = {
      // get parser
      val (parser, isInst, input) = getParser(words)

      // get code
      val code = if (input.isEmpty) {
        // read multiple lines
        var list = List[String]()
        while (scala.io.StdIn.readLine match {
          case null | "" => false
          case str => list ::= str; true
        }) ()
        list.reverse
      } else List(input.mkString(" "))

      // get tokens
      val tokens = if (raw) {
        // from raw string
        if (isInst) TokenParser.getTokens(code)
        else TokenParser.getTokens(code.mkString(" "))
      } else {
        // from tokens
        ???
      }
      println(s"[Tokens] ${tokens.mkString(" ")}")

      val result = Compiler.parseAll(parser, tokens)
      if (result.successful) {
        val resultStr = beautify(result.get, index = true)
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
              case "token" =>
                // TODO parse(false, rest)
                println("[Yet] Not yet supported `:token`")
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
