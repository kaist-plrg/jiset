package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait FunctionRestParameter extends AST { val kind: String = "FunctionRestParameter" }

case class FunctionRestParameter0(x0: BindingRestElement, parserParams: List[Boolean], span: Span) extends FunctionRestParameter {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("BindingRestElement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
