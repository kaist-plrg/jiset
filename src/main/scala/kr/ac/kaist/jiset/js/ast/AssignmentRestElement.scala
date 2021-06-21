package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait AssignmentRestElement extends AST { val kind: String = "AssignmentRestElement" }

case class AssignmentRestElement0(x1: DestructuringAssignmentTarget, parserParams: List[Boolean], span: Span) extends AssignmentRestElement {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("DestructuringAssignmentTarget", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"... $x1"
  }
}
