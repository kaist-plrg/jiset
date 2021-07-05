package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait BindingElementList extends AST { val kind: String = "BindingElementList" }

case class BindingElementList0(x0: BindingElisionElement, parserParams: List[Boolean], span: Span) extends BindingElementList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("BindingElisionElement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BindingElementList1(x0: BindingElementList, x2: BindingElisionElement, parserParams: List[Boolean], span: Span) extends BindingElementList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("BindingElisionElement", x2, l("BindingElementList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
