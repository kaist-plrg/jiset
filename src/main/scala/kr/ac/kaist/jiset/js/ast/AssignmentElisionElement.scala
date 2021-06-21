package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait AssignmentElisionElement extends AST { val kind: String = "AssignmentElisionElement" }

case class AssignmentElisionElement0(x0: Option[Elision], x1: AssignmentElement, parserParams: List[Boolean], span: Span) extends AssignmentElisionElement {
  x0.foreach((m) => m.parent = Some(this))
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("AssignmentElement", x1, l("Option[Elision]", x0, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"${x0.getOrElse("")} $x1"
  }
}
