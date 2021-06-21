package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait MultiplicativeExpression extends AST { val kind: String = "MultiplicativeExpression" }

case class MultiplicativeExpression0(x0: ExponentiationExpression, parserParams: List[Boolean], span: Span) extends MultiplicativeExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ExponentiationExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class MultiplicativeExpression1(x0: MultiplicativeExpression, x1: MultiplicativeOperator, x2: ExponentiationExpression, parserParams: List[Boolean], span: Span) extends MultiplicativeExpression {
  x0.parent = Some(this)
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x1, d(x0, 0)))
  def fullList: List[(String, Value)] = l("ExponentiationExpression", x2, l("MultiplicativeOperator", x1, l("MultiplicativeExpression", x0, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1 $x2"
  }
}
