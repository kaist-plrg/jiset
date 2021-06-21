package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait AssignmentPropertyList extends AST { val kind: String = "AssignmentPropertyList" }

case class AssignmentPropertyList0(x0: AssignmentProperty, parserParams: List[Boolean], span: Span) extends AssignmentPropertyList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("AssignmentProperty", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class AssignmentPropertyList1(x0: AssignmentPropertyList, x2: AssignmentProperty, parserParams: List[Boolean], span: Span) extends AssignmentPropertyList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("AssignmentProperty", x2, l("AssignmentPropertyList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
