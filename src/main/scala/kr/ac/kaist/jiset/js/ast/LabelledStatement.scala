package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait LabelledStatement extends AST { val kind: String = "LabelledStatement" }

case class LabelledStatement0(x0: LabelIdentifier, x2: LabelledItem, parserParams: List[Boolean], span: Span) extends LabelledStatement {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("LabelledItem", x2, l("LabelIdentifier", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 : $x2"
  }
}
