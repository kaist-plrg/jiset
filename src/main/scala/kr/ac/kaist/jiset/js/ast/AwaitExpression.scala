package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait AwaitExpression extends AST { val kind: String = "AwaitExpression" }

case class AwaitExpression0(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends AwaitExpression {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"await $x1"
  }
}
