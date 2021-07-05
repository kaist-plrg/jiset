package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait AdditiveExpression extends AST { val kind: String = "AdditiveExpression" }

case class AdditiveExpression0(x0: MultiplicativeExpression, parserParams: List[Boolean], span: Span) extends AdditiveExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("MultiplicativeExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class AdditiveExpression1(x0: AdditiveExpression, x2: MultiplicativeExpression, parserParams: List[Boolean], span: Span) extends AdditiveExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("MultiplicativeExpression", x2, l("AdditiveExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 + $x2"
  }
}

case class AdditiveExpression2(x0: AdditiveExpression, x2: MultiplicativeExpression, parserParams: List[Boolean], span: Span) extends AdditiveExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("MultiplicativeExpression", x2, l("AdditiveExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 - $x2"
  }
}
