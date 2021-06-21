package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ObjectAssignmentPattern extends AST { val kind: String = "ObjectAssignmentPattern" }

case class ObjectAssignmentPattern0(parserParams: List[Boolean], span: Span) extends ObjectAssignmentPattern {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ }"
  }
}

case class ObjectAssignmentPattern1(x1: AssignmentRestProperty, parserParams: List[Boolean], span: Span) extends ObjectAssignmentPattern {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("AssignmentRestProperty", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}

case class ObjectAssignmentPattern2(x1: AssignmentPropertyList, parserParams: List[Boolean], span: Span) extends ObjectAssignmentPattern {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("AssignmentPropertyList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}

case class ObjectAssignmentPattern3(x1: AssignmentPropertyList, x3: Option[AssignmentRestProperty], parserParams: List[Boolean], span: Span) extends ObjectAssignmentPattern {
  x1.parent = Some(this)
  x3.foreach((m) => m.parent = Some(this))
  def idx: Int = 3
  def k: Int = d(x3, d(x1, 0))
  def fullList: List[(String, Value)] = l("Option[AssignmentRestProperty]", x3, l("AssignmentPropertyList", x1, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"{ $x1 , ${x3.getOrElse("")} }"
  }
}
