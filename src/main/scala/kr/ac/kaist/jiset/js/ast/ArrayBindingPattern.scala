package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ArrayBindingPattern extends AST { val kind: String = "ArrayBindingPattern" }

case class ArrayBindingPattern0(x1: Option[Elision], x2: Option[BindingRestElement], parserParams: List[Boolean], span: Span) extends ArrayBindingPattern {
  x1.foreach((m) => m.parent = Some(this))
  x2.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, Value)] = l("Option[BindingRestElement]", x2, l("Option[Elision]", x1, Nil)).reverse
  def maxK: Int = 3
  override def toString: String = {
    s"[ ${x1.getOrElse("")} ${x2.getOrElse("")} ]"
  }
}

case class ArrayBindingPattern1(x1: BindingElementList, parserParams: List[Boolean], span: Span) extends ArrayBindingPattern {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("BindingElementList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"[ $x1 ]"
  }
}

case class ArrayBindingPattern2(x1: BindingElementList, x3: Option[Elision], x4: Option[BindingRestElement], parserParams: List[Boolean], span: Span) extends ArrayBindingPattern {
  x1.parent = Some(this)
  x3.foreach((m) => m.parent = Some(this))
  x4.foreach((m) => m.parent = Some(this))
  def idx: Int = 2
  def k: Int = d(x4, d(x3, d(x1, 0)))
  def fullList: List[(String, Value)] = l("Option[BindingRestElement]", x4, l("Option[Elision]", x3, l("BindingElementList", x1, Nil))).reverse
  def maxK: Int = 3
  override def toString: String = {
    s"[ $x1 , ${x3.getOrElse("")} ${x4.getOrElse("")} ]"
  }
}
