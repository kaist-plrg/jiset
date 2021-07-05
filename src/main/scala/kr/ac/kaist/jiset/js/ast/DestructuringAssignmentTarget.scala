package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait DestructuringAssignmentTarget extends AST { val kind: String = "DestructuringAssignmentTarget" }

case class DestructuringAssignmentTarget0(x0: LeftHandSideExpression, parserParams: List[Boolean], span: Span) extends DestructuringAssignmentTarget {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("LeftHandSideExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
