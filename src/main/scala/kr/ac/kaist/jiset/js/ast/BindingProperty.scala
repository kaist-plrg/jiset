package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait BindingProperty extends AST { val kind: String = "BindingProperty" }

case class BindingProperty0(x0: SingleNameBinding, parserParams: List[Boolean], span: Span) extends BindingProperty {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("SingleNameBinding", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BindingProperty1(x0: PropertyName, x2: BindingElement, parserParams: List[Boolean], span: Span) extends BindingProperty {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("BindingElement", x2, l("PropertyName", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 : $x2"
  }
}
