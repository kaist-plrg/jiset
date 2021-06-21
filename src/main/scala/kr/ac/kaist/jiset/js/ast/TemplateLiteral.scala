package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait TemplateLiteral extends AST { val kind: String = "TemplateLiteral" }

case class TemplateLiteral0(x0: Lexical, parserParams: List[Boolean], span: Span) extends TemplateLiteral {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class TemplateLiteral1(x0: SubstitutionTemplate, parserParams: List[Boolean], span: Span) extends TemplateLiteral {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("SubstitutionTemplate", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
