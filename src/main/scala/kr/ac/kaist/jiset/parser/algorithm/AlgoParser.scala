package kr.ac.kaist.jiset.parser.algorithm

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._

object AlgoParser {
  // get algorithms
  def apply(
    elem: Element,
    detail: Boolean = false
  )(
    implicit
    lines: Array[String],
    grammar: Grammar,
    region: Region,
    document: Document
  ): List[Algo] = {
    if (detail) println(s"==================================================")
    val result = try {
      val heads = HeadParser(elem)
      val (start, end) = getRange(elem).get
      // get code
      val code = getRawBody(elem)
      // get tokens
      val tokens = TokenParser.getTokens(code)
      // get body
      val rawBody = Compiler(tokens, start)

      // print detail
      if (detail) {
        println(s"Range: (${start + 1}, $end)")
        code.foreach(println _)
        println(s"--------------------------------------------------")
        heads.foreach(println(_))
        println("====>")
        println(beautify(rawBody, index = true))
      }

      heads.map(Algo(_, rawBody, code))
    } catch {
      case e: Throwable =>
        if (detail) {
          println(s"[Algo] ${e.getMessage}")
          e.getStackTrace.foreach(println _)
        }
        Nil
    }
    if (detail) println(s"==================================================")
    result
  }
}
