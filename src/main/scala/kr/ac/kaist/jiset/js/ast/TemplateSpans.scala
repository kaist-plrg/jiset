package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait TemplateSpans extends AST { val kind: String = "TemplateSpans" }

object TemplateSpans {
  def apply(data: Json): TemplateSpans = AST(data) match {
    case Some(compressed) => TemplateSpans(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): TemplateSpans = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Lexical(_)).get
        TemplateSpans0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(TemplateMiddleList(_)).get
        val x1 = subs(1).map(Lexical(_)).get
        TemplateSpans1(x0, x1, params, span)
    }
  }
}

case class TemplateSpans0(x0: Lexical, parserParams: List[Boolean], span: Span) extends TemplateSpans {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class TemplateSpans1(x0: TemplateMiddleList, x1: Lexical, parserParams: List[Boolean], span: Span) extends TemplateSpans {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("Lexical", x1, l("TemplateMiddleList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
