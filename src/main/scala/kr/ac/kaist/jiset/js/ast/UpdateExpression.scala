package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait UpdateExpression extends AST { val kind: String = "UpdateExpression" }

case class UpdateExpression0(x0: LeftHandSideExpression, parserParams: List[Boolean], span: Span) extends UpdateExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("LeftHandSideExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class UpdateExpression1(x0: LeftHandSideExpression, parserParams: List[Boolean], span: Span) extends UpdateExpression {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("LeftHandSideExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ++"
  }
}

case class UpdateExpression2(x0: LeftHandSideExpression, parserParams: List[Boolean], span: Span) extends UpdateExpression {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("LeftHandSideExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 --"
  }
}

case class UpdateExpression3(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UpdateExpression {
  x1.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"++ $x1"
  }
}

case class UpdateExpression4(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UpdateExpression {
  x1.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"-- $x1"
  }
}
