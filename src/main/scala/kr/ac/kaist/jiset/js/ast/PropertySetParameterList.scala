package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait PropertySetParameterList extends AST { val kind: String = "PropertySetParameterList" }

case class PropertySetParameterList0(x0: FormalParameter, parserParams: List[Boolean], span: Span) extends PropertySetParameterList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("FormalParameter", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
