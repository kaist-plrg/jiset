package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait CoalesceExpression extends AST { val kind: String = "CoalesceExpression" }

case class CoalesceExpression0(x0: CoalesceExpressionHead, x2: BitwiseORExpression, parserParams: List[Boolean], span: Span) extends CoalesceExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("BitwiseORExpression", x2, l("CoalesceExpressionHead", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ?? $x2"
  }
}
