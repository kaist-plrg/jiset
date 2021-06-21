package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait BindingPropertyList extends AST { val kind: String = "BindingPropertyList" }

case class BindingPropertyList0(x0: BindingProperty, parserParams: List[Boolean], span: Span) extends BindingPropertyList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("BindingProperty", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BindingPropertyList1(x0: BindingPropertyList, x2: BindingProperty, parserParams: List[Boolean], span: Span) extends BindingPropertyList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("BindingProperty", x2, l("BindingPropertyList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
