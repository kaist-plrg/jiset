package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ExponentiationExpression extends AST { val kind: String = "ExponentiationExpression" }

case class ExponentiationExpression0(x0: UnaryExpression, parserParams: List[Boolean], span: Span) extends ExponentiationExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("UnaryExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ExponentiationExpression1(x0: UpdateExpression, x2: ExponentiationExpression, parserParams: List[Boolean], span: Span) extends ExponentiationExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("ExponentiationExpression", x2, l("UpdateExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ** $x2"
  }
}
