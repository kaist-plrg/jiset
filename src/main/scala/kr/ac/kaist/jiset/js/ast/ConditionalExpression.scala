package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ConditionalExpression extends AST { val kind: String = "ConditionalExpression" }

case class ConditionalExpression0(x0: ShortCircuitExpression, parserParams: List[Boolean], span: Span) extends ConditionalExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ShortCircuitExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ConditionalExpression1(x0: ShortCircuitExpression, x2: AssignmentExpression, x4: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ConditionalExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  x4.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x4, d(x2, d(x0, 0)))
  def fullList: List[(String, Value)] = l("AssignmentExpression1", x4, l("AssignmentExpression0", x2, l("ShortCircuitExpression", x0, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ? $x2 : $x4"
  }
}
