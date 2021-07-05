package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait LeftHandSideExpression extends AST { val kind: String = "LeftHandSideExpression" }

case class LeftHandSideExpression0(x0: NewExpression, parserParams: List[Boolean], span: Span) extends LeftHandSideExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("NewExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class LeftHandSideExpression1(x0: CallExpression, parserParams: List[Boolean], span: Span) extends LeftHandSideExpression {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("CallExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class LeftHandSideExpression2(x0: OptionalExpression, parserParams: List[Boolean], span: Span) extends LeftHandSideExpression {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("OptionalExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
