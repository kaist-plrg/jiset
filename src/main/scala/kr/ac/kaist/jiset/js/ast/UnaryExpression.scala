package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait UnaryExpression extends AST { val kind: String = "UnaryExpression" }

case class UnaryExpression0(x0: UpdateExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("UpdateExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class UnaryExpression1(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"delete $x1"
  }
}

case class UnaryExpression2(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"void $x1"
  }
}

case class UnaryExpression3(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x1.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"typeof $x1"
  }
}

case class UnaryExpression4(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x1.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"+ $x1"
  }
}

case class UnaryExpression5(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x1.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"- $x1"
  }
}

case class UnaryExpression6(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x1.parent = Some(this)
  def idx: Int = 6
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"~ $x1"
  }
}

case class UnaryExpression7(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x1.parent = Some(this)
  def idx: Int = 7
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"! $x1"
  }
}

case class UnaryExpression8(x0: AwaitExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x0.parent = Some(this)
  def idx: Int = 8
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("AwaitExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
