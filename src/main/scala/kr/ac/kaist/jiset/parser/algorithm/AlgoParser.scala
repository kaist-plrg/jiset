package kr.ac.kaist.jiset.parser.algorithm

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.ir.Beautifier._
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
    if (detail) println(s"==================================================")
    val result = try {
      val (start, end) = getRange(elem).get
      // get code
      val code = getRawBody(elem)
      // get tokens
      val tokens = TokenParser.getTokens(code, secIds)
      // get body
      val rawBody =
        if (useCount) CompileRuleCounter(tokens, start)
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
        println(beautify(rawBody, index = true)) // TODO print exprId, currently generated later so all -1
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
    result.foreach(algo => UidWalker.walk(algo.rawBody))
    result
  }

  object UidWalker extends UnitWalker {
    private var count: Int = 0
    private def getCount: Int = { val result = count; count += 1; result }
    override def walk(expr: Expr): Unit = {
      expr match {
        case expr: AllocExpr => expr.asite = getCount
        case _ =>
      }
      super.walk(expr)
    }
  }
}
