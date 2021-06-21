package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait YieldExpression extends AST { val kind: String = "YieldExpression" }

case class YieldExpression0(parserParams: List[Boolean], span: Span) extends YieldExpression {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"yield"
  }
}

case class YieldExpression1(x2: AssignmentExpression, parserParams: List[Boolean], span: Span) extends YieldExpression {
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("AssignmentExpression", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"yield $x2"
  }
}

case class YieldExpression2(x3: AssignmentExpression, parserParams: List[Boolean], span: Span) extends YieldExpression {
  x3.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x3, 0)
  def fullList: List[(String, Value)] = l("AssignmentExpression", x3, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"yield * $x3"
  }
}
