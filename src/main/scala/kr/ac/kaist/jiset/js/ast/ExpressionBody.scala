package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ExpressionBody extends AST { val kind: String = "ExpressionBody" }

case class ExpressionBody0(x0: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ExpressionBody {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("AssignmentExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
