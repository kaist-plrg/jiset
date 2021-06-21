package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait TemplateMiddleList extends AST { val kind: String = "TemplateMiddleList" }

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
