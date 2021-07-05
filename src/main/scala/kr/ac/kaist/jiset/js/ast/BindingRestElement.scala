package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait BindingRestElement extends AST { val kind: String = "BindingRestElement" }

case class BindingRestElement0(x1: BindingIdentifier, parserParams: List[Boolean], span: Span) extends BindingRestElement {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("BindingIdentifier", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"... $x1"
  }
}

case class BindingRestElement1(x1: BindingPattern, parserParams: List[Boolean], span: Span) extends BindingRestElement {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("BindingPattern", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"... $x1"
  }
}
