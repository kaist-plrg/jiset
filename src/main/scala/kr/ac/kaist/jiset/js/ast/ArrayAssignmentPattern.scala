package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ArrayAssignmentPattern extends AST { val kind: String = "ArrayAssignmentPattern" }

case class ArrayAssignmentPattern0(x1: Option[Elision], x2: Option[AssignmentRestElement], parserParams: List[Boolean], span: Span) extends ArrayAssignmentPattern {
  x1.foreach((m) => m.parent = Some(this))
  x2.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, Value)] = l("Option[AssignmentRestElement]", x2, l("Option[Elision]", x1, Nil)).reverse
  def maxK: Int = 3
  override def toString: String = {
    s"[ ${x1.getOrElse("")} ${x2.getOrElse("")} ]"
  }
}

case class ArrayAssignmentPattern1(x1: AssignmentElementList, parserParams: List[Boolean], span: Span) extends ArrayAssignmentPattern {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("AssignmentElementList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"[ $x1 ]"
  }
}

case class ArrayAssignmentPattern2(x1: AssignmentElementList, x3: Option[Elision], x4: Option[AssignmentRestElement], parserParams: List[Boolean], span: Span) extends ArrayAssignmentPattern {
  x1.parent = Some(this)
  x3.foreach((m) => m.parent = Some(this))
  x4.foreach((m) => m.parent = Some(this))
  def idx: Int = 2
  def k: Int = d(x4, d(x3, d(x1, 0)))
  def fullList: List[(String, Value)] = l("Option[AssignmentRestElement]", x4, l("Option[Elision]", x3, l("AssignmentElementList", x1, Nil))).reverse
  def maxK: Int = 3
  override def toString: String = {
    s"[ $x1 , ${x3.getOrElse("")} ${x4.getOrElse("")} ]"
  }
}
