package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait AsyncArrowBindingIdentifier extends AST { val kind: String = "AsyncArrowBindingIdentifier" }

case class AsyncArrowBindingIdentifier0(x0: BindingIdentifier, parserParams: List[Boolean], span: Span) extends AsyncArrowBindingIdentifier {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("BindingIdentifier", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
