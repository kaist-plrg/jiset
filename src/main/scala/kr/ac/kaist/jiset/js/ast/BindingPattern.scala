package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait BindingPattern extends AST { val kind: String = "BindingPattern" }

case class BindingPattern0(x0: ObjectBindingPattern, parserParams: List[Boolean], span: Span) extends BindingPattern {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ObjectBindingPattern", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BindingPattern1(x0: ArrayBindingPattern, parserParams: List[Boolean], span: Span) extends BindingPattern {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ArrayBindingPattern", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
