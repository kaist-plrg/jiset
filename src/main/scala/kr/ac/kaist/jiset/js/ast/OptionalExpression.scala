package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait OptionalExpression extends AST { val kind: String = "OptionalExpression" }

case class OptionalExpression0(x0: MemberExpression, x1: OptionalChain, parserParams: List[Boolean], span: Span) extends OptionalExpression {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("OptionalChain", x1, l("MemberExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}

case class OptionalExpression1(x0: CallExpression, x1: OptionalChain, parserParams: List[Boolean], span: Span) extends OptionalExpression {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("OptionalChain", x1, l("CallExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}

case class OptionalExpression2(x0: OptionalExpression, x1: OptionalChain, parserParams: List[Boolean], span: Span) extends OptionalExpression {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("OptionalChain", x1, l("OptionalExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
