package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ObjectBindingPattern extends AST { val kind: String = "ObjectBindingPattern" }

case class ObjectBindingPattern0(parserParams: List[Boolean], span: Span) extends ObjectBindingPattern {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ }"
  }
}

case class ObjectBindingPattern1(x1: BindingRestProperty, parserParams: List[Boolean], span: Span) extends ObjectBindingPattern {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("BindingRestProperty", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}

case class ObjectBindingPattern2(x1: BindingPropertyList, parserParams: List[Boolean], span: Span) extends ObjectBindingPattern {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("BindingPropertyList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}

case class ObjectBindingPattern3(x1: BindingPropertyList, x3: Option[BindingRestProperty], parserParams: List[Boolean], span: Span) extends ObjectBindingPattern {
  x1.parent = Some(this)
  x3.foreach((m) => m.parent = Some(this))
  def idx: Int = 3
  def k: Int = d(x3, d(x1, 0))
  def fullList: List[(String, Value)] = l("Option[BindingRestProperty]", x3, l("BindingPropertyList", x1, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"{ $x1 , ${x3.getOrElse("")} }"
  }
}
