package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait PropertyDefinition extends AST { val kind: String = "PropertyDefinition" }

case class PropertyDefinition0(x0: IdentifierReference, parserParams: List[Boolean], span: Span) extends PropertyDefinition {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("IdentifierReference", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PropertyDefinition1(x0: CoverInitializedName, parserParams: List[Boolean], span: Span) extends PropertyDefinition {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("CoverInitializedName", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PropertyDefinition2(x0: PropertyName, x2: AssignmentExpression, parserParams: List[Boolean], span: Span) extends PropertyDefinition {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("AssignmentExpression", x2, l("PropertyName", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 : $x2"
  }
}

case class PropertyDefinition3(x0: MethodDefinition, parserParams: List[Boolean], span: Span) extends PropertyDefinition {
  x0.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("MethodDefinition", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PropertyDefinition4(x1: AssignmentExpression, parserParams: List[Boolean], span: Span) extends PropertyDefinition {
  x1.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("AssignmentExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"... $x1"
  }
}
