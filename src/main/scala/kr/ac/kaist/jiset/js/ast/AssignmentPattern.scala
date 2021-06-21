package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait AssignmentPattern extends AST { val kind: String = "AssignmentPattern" }

case class AssignmentPattern0(x0: ObjectAssignmentPattern, parserParams: List[Boolean], span: Span) extends AssignmentPattern {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ObjectAssignmentPattern", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class AssignmentPattern1(x0: ArrayAssignmentPattern, parserParams: List[Boolean], span: Span) extends AssignmentPattern {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ArrayAssignmentPattern", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
