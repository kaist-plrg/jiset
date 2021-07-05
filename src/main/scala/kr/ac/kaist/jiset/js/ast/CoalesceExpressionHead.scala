package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait CoalesceExpressionHead extends AST { val kind: String = "CoalesceExpressionHead" }

case class CoalesceExpressionHead0(x0: CoalesceExpression, parserParams: List[Boolean], span: Span) extends CoalesceExpressionHead {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("CoalesceExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class CoalesceExpressionHead1(x0: BitwiseORExpression, parserParams: List[Boolean], span: Span) extends CoalesceExpressionHead {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("BitwiseORExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
