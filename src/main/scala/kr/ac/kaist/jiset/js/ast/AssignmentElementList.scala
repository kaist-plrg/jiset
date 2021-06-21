package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait AssignmentElementList extends AST { val kind: String = "AssignmentElementList" }

case class AssignmentElementList0(x0: AssignmentElisionElement, parserParams: List[Boolean], span: Span) extends AssignmentElementList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("AssignmentElisionElement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class AssignmentElementList1(x0: AssignmentElementList, x2: AssignmentElisionElement, parserParams: List[Boolean], span: Span) extends AssignmentElementList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("AssignmentElisionElement", x2, l("AssignmentElementList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
