package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait SuperCall extends AST { val kind: String = "SuperCall" }

case class SuperCall0(x1: Arguments, parserParams: List[Boolean], span: Span) extends SuperCall {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Arguments", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"super $x1"
  }
}
