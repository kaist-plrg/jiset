package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ShortCircuitExpression extends AST { val kind: String = "ShortCircuitExpression" }

case class ShortCircuitExpression0(x0: LogicalORExpression, parserParams: List[Boolean], span: Span) extends ShortCircuitExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("LogicalORExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ShortCircuitExpression1(x0: CoalesceExpression, parserParams: List[Boolean], span: Span) extends ShortCircuitExpression {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("CoalesceExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
