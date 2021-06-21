package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait StatementListItem extends AST { val kind: String = "StatementListItem" }

case class StatementListItem0(x0: Statement, parserParams: List[Boolean], span: Span) extends StatementListItem {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("Statement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class StatementListItem1(x0: Declaration, parserParams: List[Boolean], span: Span) extends StatementListItem {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("Declaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
