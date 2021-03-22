package kr.ac.kaist.jiset.parser.algorithm

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.spec.{ ECMAScript, Region }
import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.spec.grammar.Grammar
import kr.ac.kaist.jiset.util.Useful._
import org.jsoup.nodes._

object AlgoParser {
  // get algorithms
  def apply(
    parsedHead: (Element, List[Head]),
    secIds: Map[String, Name],
    useCount: Boolean,
    detail: Boolean
  )(
    implicit
    lines: Array[String],
    grammar: Grammar,
    region: Region,
    document: Document
  ): List[Algo] = {
    val (elem, heads) = parsedHead
    val ids: List[String] = getIds(elem)
    if (detail) println(s"==================================================")
    val result = try {
      val (start, end) = getRange(elem).get
      // get code
      val code = getRawBody(elem)
      // get tokens
      val tokens = TokenParser.getTokens(code, secIds)
      // get body
      val rawBody =
        if (useCount) ??? // TODO compiler with counters
        else Compiler(tokens, start)

      // print detail
      if (detail) {
        println(s"Range: (${start + 1}, $end)")
        code.foreach(println _)
        println(s"--------------------------------------------------")
        println(Token.getString(tokens))
        println(s"--------------------------------------------------")
        heads.foreach(println(_))
        println("====>")
        println(rawBody.beautified)
      }

      heads.map(Algo(_, ids, rawBody, code))
    } catch {
      case e: Throwable =>
        if (detail) {
          println(s"[Algo] ${e.getMessage}")
          e.getStackTrace.foreach(println _)
        }
        Nil
    }
    if (detail) println(s"==================================================")
    result.foreach(algo => (new LocWalker).walk(algo.rawBody))
    result
  }

  // get ancestor ids
  def getIds(elem: Element): List[String] = {
    val ids =
      if (elem.parent == null) Nil
      else getIds(elem.parent)
    if (elem.id == "") ids
    else elem.id :: ids
  }
}
