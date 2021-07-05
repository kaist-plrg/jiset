package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait MemberExpression extends AST { val kind: String = "MemberExpression" }

case class MemberExpression0(x0: PrimaryExpression, parserParams: List[Boolean], span: Span) extends MemberExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("PrimaryExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class MemberExpression1(x0: MemberExpression, x2: Expression, parserParams: List[Boolean], span: Span) extends MemberExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("Expression", x2, l("MemberExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 [ $x2 ]"
  }
}

case class MemberExpression2(x0: MemberExpression, x2: Lexical, parserParams: List[Boolean], span: Span) extends MemberExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("Lexical", x2, l("MemberExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 . $x2"
  }
}

case class MemberExpression3(x0: MemberExpression, x1: TemplateLiteral, parserParams: List[Boolean], span: Span) extends MemberExpression {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("TemplateLiteral", x1, l("MemberExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}

case class MemberExpression4(x0: SuperProperty, parserParams: List[Boolean], span: Span) extends MemberExpression {
  x0.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("SuperProperty", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class MemberExpression5(x0: MetaProperty, parserParams: List[Boolean], span: Span) extends MemberExpression {
  x0.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("MetaProperty", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class MemberExpression6(x1: MemberExpression, x2: Arguments, parserParams: List[Boolean], span: Span) extends MemberExpression {
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 6
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, Value)] = l("Arguments", x2, l("MemberExpression", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"new $x1 $x2"
  }
}
