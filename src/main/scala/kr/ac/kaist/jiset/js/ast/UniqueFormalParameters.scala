package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait UniqueFormalParameters extends AST { val kind: String = "UniqueFormalParameters" }

case class UniqueFormalParameters0(x0: FormalParameters, parserParams: List[Boolean], span: Span) extends UniqueFormalParameters {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("FormalParameters", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
