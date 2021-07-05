package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ArgumentList extends AST { val kind: String = "ArgumentList" }

case class ArgumentList0(x0: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ArgumentList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("AssignmentExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ArgumentList1(x1: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ArgumentList {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("AssignmentExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"... $x1"
  }
}

case class ArgumentList2(x0: ArgumentList, x2: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ArgumentList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("AssignmentExpression", x2, l("ArgumentList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}

case class ArgumentList3(x0: ArgumentList, x3: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ArgumentList {
  x0.parent = Some(this)
  x3.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x3, d(x0, 0))
  def fullList: List[(String, Value)] = l("AssignmentExpression", x3, l("ArgumentList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , ... $x3"
  }
}
