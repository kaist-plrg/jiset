package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait EqualityExpression extends AST { val kind: String = "EqualityExpression" }

case class EqualityExpression0(x0: RelationalExpression, parserParams: List[Boolean], span: Span) extends EqualityExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("RelationalExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class EqualityExpression1(x0: EqualityExpression, x2: RelationalExpression, parserParams: List[Boolean], span: Span) extends EqualityExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("RelationalExpression", x2, l("EqualityExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 == $x2"
  }
}

case class EqualityExpression2(x0: EqualityExpression, x2: RelationalExpression, parserParams: List[Boolean], span: Span) extends EqualityExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("RelationalExpression", x2, l("EqualityExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 != $x2"
  }
}

case class EqualityExpression3(x0: EqualityExpression, x2: RelationalExpression, parserParams: List[Boolean], span: Span) extends EqualityExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("RelationalExpression", x2, l("EqualityExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 === $x2"
  }
}

case class EqualityExpression4(x0: EqualityExpression, x2: RelationalExpression, parserParams: List[Boolean], span: Span) extends EqualityExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("RelationalExpression", x2, l("EqualityExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 !== $x2"
  }
}
