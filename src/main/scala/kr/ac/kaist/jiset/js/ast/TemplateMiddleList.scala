package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait TemplateMiddleList extends AST { val kind: String = "TemplateMiddleList" }

object TemplateMiddleList {
  def apply(data: Json): TemplateMiddleList = AST(data) match {
    case Some(compressed) => TemplateMiddleList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): TemplateMiddleList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Lexical(_)).get
        val x1 = subs(1).map(Expression(_)).get
        TemplateMiddleList0(x0, x1, params, span)
      case 1 =>
        val x0 = subs(0).map(TemplateMiddleList(_)).get
        val x1 = subs(1).map(Lexical(_)).get
        val x2 = subs(2).map(Expression(_)).get
        TemplateMiddleList1(x0, x1, x2, params, span)
    }
  }
}

case class TemplateMiddleList0(x0: Lexical, x1: Expression, parserParams: List[Boolean], span: Span) extends TemplateMiddleList {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("Expression", x1, l("Lexical", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}

case class TemplateMiddleList1(x0: TemplateMiddleList, x1: Lexical, x2: Expression, parserParams: List[Boolean], span: Span) extends TemplateMiddleList {
  x0.parent = Some(this)
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x1, d(x0, 0)))
  def fullList: List[(String, Value)] = l("Expression", x2, l("Lexical", x1, l("TemplateMiddleList", x0, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1 $x2"
  }
}
