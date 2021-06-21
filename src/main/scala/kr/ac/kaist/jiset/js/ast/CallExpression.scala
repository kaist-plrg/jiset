package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait CallExpression extends AST { val kind: String = "CallExpression" }

case class CallExpression0(x0: CoverCallExpressionAndAsyncArrowHead, parserParams: List[Boolean], span: Span) extends CallExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("CoverCallExpressionAndAsyncArrowHead", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class CallExpression1(x0: SuperCall, parserParams: List[Boolean], span: Span) extends CallExpression {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("SuperCall", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class CallExpression2(x0: ImportCall, parserParams: List[Boolean], span: Span) extends CallExpression {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ImportCall", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class CallExpression3(x0: CallExpression, x1: Arguments, parserParams: List[Boolean], span: Span) extends CallExpression {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("Arguments", x1, l("CallExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}

case class CallExpression4(x0: CallExpression, x2: Expression, parserParams: List[Boolean], span: Span) extends CallExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("Expression", x2, l("CallExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 [ $x2 ]"
  }
}

case class CallExpression5(x0: CallExpression, x2: Lexical, parserParams: List[Boolean], span: Span) extends CallExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("Lexical", x2, l("CallExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 . $x2"
  }
}

case class CallExpression6(x0: CallExpression, x1: TemplateLiteral, parserParams: List[Boolean], span: Span) extends CallExpression {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 6
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("TemplateLiteral", x1, l("CallExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
