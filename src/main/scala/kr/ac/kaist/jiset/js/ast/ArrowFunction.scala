package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ArrowFunction extends AST { val kind: String = "ArrowFunction" }

case class ArrowFunction0(x0: ArrowParameters, x3: ConciseBody, parserParams: List[Boolean], span: Span) extends ArrowFunction {
  x0.parent = Some(this)
  x3.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x3, d(x0, 0))
  def fullList: List[(String, Value)] = l("ConciseBody", x3, l("ArrowParameters", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 => $x3"
  }
}
