package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.parser.algorithm.{ TokenParser, Compiler }
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

object CompileREPL {
  def run(implicit grammar: Grammar, document: Document): Unit = {
    val cyan = "\u001b[36m"
    val reset = "\u001b[0m"
    val builder: TerminalBuilder = TerminalBuilder.builder()
    val terminal: Terminal = builder.build()
    val completer: TreeCompleter = new TreeCompleter(
      node("eof"),
      node("buffer")
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

    def prompt: String = LINE_SEP + s"${cyan}jiset>${reset} "

    var keep: Boolean = true
    var lbuf = LineBuffer

    while (keep) {
      // reader
      try {
        trimRight(reader.readLine(prompt)) match {
          case null =>
          case "exit" => keep = false
          case "buffer" => println(lbuf)
          case "eof" => {
            if (!lbuf.isEmpty) {
              // print buffer
              println("[Buffer]")
              println(lbuf)
              println
              // tokenize
              val tokens = TokenParser.getTokens(lbuf.buffer)
              // compile
              val inst = Compiler(tokens)
              // print compiled instruction
              println("[Result]")
              println(beautify(inst))
              println
              // reset buffer
              lbuf.reset
            }
            terminal.flush()
          }
          case line => lbuf.append(line)
        }
      } catch {
        case e: EndOfFileException => keep = false
        case e: UserInterruptException => keep = false
        case e: Throwable => {
          lbuf.reset
          stopMessage(s"ERROR: $e")
        }
      }
    }
  }

  case object LineBuffer {
    var buffer: Array[String] = Array()

    def isEmpty: Boolean = { buffer.isEmpty }

    def append(line: String): Unit =
      buffer ++= line.split(LINE_SEP).filter(_.trim != "")

    def reset: Unit = { buffer = Array() }

    override def toString: String =
      if (buffer.isEmpty) "empty"
      else buffer.zipWithIndex.map {
        case (line, i) => s"[$i] $line"
      }.mkString(LINE_SEP)
  }

}
