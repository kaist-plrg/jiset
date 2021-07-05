package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ConciseBody extends AST { val kind: String = "ConciseBody" }

case class ConciseBody0(x1: ExpressionBody, parserParams: List[Boolean], span: Span) extends ConciseBody {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("ExpressionBody", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x1"
  }
}

case class ConciseBody1(x1: FunctionBody, parserParams: List[Boolean], span: Span) extends ConciseBody {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("FunctionBody", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}
