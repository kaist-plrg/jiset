package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ParenthesizedExpression extends AST { val kind: String = "ParenthesizedExpression" }

case class ParenthesizedExpression0(x1: Expression, parserParams: List[Boolean], span: Span) extends ParenthesizedExpression {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Expression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( $x1 )"
  }
}
