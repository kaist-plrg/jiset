package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ArrowFormalParameters extends AST { val kind: String = "ArrowFormalParameters" }

case class ArrowFormalParameters0(x1: UniqueFormalParameters, parserParams: List[Boolean], span: Span) extends ArrowFormalParameters {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("UniqueFormalParameters", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( $x1 )"
  }
}
