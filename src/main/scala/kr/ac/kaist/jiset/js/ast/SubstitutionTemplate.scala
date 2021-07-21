package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait SubstitutionTemplate extends AST { val kind: String = "SubstitutionTemplate" }

object SubstitutionTemplate {
  def apply(data: Json): SubstitutionTemplate = AST(data) match {
    case Some(compressed) => SubstitutionTemplate(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): SubstitutionTemplate = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Lexical(_)).get
        val x1 = subs(1).map(Expression(_)).get
        val x2 = subs(2).map(TemplateSpans(_)).get
        SubstitutionTemplate0(x0, x1, x2, params, span)
    }
  }
}

case class SubstitutionTemplate0(x0: Lexical, x1: Expression, x2: TemplateSpans, parserParams: List[Boolean], span: Span) extends SubstitutionTemplate {
  x0.parent = Some(this)
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, d(x1, d(x0, 0)))
  def fullList: List[(String, Value)] = l("TemplateSpans", x2, l("Expression", x1, l("Lexical", x0, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1 $x2"
  }
}
