package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait NewExpression extends AST { val kind: String = "NewExpression" }

case class NewExpression0(x0: MemberExpression, parserParams: List[Boolean], span: Span) extends NewExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("MemberExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class NewExpression1(x1: NewExpression, parserParams: List[Boolean], span: Span) extends NewExpression {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("NewExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"new $x1"
  }
}
