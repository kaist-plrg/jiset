package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ExpressionStatement extends AST { val kind: String = "ExpressionStatement" }

case class ExpressionStatement0(x1: Expression, parserParams: List[Boolean], span: Span) extends ExpressionStatement {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Expression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x1 ;"
  }
}
