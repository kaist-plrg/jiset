package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait BitwiseORExpression extends AST { val kind: String = "BitwiseORExpression" }

case class BitwiseORExpression0(x0: BitwiseXORExpression, parserParams: List[Boolean], span: Span) extends BitwiseORExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("BitwiseXORExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BitwiseORExpression1(x0: BitwiseORExpression, x2: BitwiseXORExpression, parserParams: List[Boolean], span: Span) extends BitwiseORExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("BitwiseXORExpression", x2, l("BitwiseORExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 | $x2"
  }
}
