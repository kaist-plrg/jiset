package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait AsyncConciseBody extends AST { val kind: String = "AsyncConciseBody" }

case class AsyncConciseBody0(x1: ExpressionBody, parserParams: List[Boolean], span: Span) extends AsyncConciseBody {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("ExpressionBody", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x1"
  }
}

case class AsyncConciseBody1(x1: AsyncFunctionBody, parserParams: List[Boolean], span: Span) extends AsyncConciseBody {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("AsyncFunctionBody", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}
