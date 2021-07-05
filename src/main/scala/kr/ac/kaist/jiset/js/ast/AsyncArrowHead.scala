package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait AsyncArrowHead extends AST { val kind: String = "AsyncArrowHead" }

case class AsyncArrowHead0(x2: ArrowFormalParameters, parserParams: List[Boolean], span: Span) extends AsyncArrowHead {
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("ArrowFormalParameters", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"async $x2"
  }
}
