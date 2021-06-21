package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait TemplateSpans extends AST { val kind: String = "TemplateSpans" }

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
