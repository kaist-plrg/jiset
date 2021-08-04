package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait PropertyDefinition extends AST { val kind: String = "PropertyDefinition" }

object PropertyDefinition {
  def apply(data: Json): PropertyDefinition = AST(data) match {
    case Some(compressed) => PropertyDefinition(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): PropertyDefinition = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(IdentifierReference(_)).get
        PropertyDefinition0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(CoverInitializedName(_)).get
        PropertyDefinition1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(PropertyName(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        PropertyDefinition2(x0, x1, params, span)
      case 3 =>
        val x0 = subs(0).map(MethodDefinition(_)).get
        PropertyDefinition3(x0, params, span)
      case 4 =>
        val x0 = subs(0).map(AssignmentExpression(_)).get
        PropertyDefinition4(x0, params, span)
    }
  }
}

case class PropertyDefinition0(x0: IdentifierReference, parserParams: List[Boolean], span: Span) extends PropertyDefinition {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("IdentifierReference", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PropertyDefinition1(x0: CoverInitializedName, parserParams: List[Boolean], span: Span) extends PropertyDefinition {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("CoverInitializedName", x0, Nil).reverse
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
  def fullList: List[(String, PureValue)] = l("AssignmentExpression", x2, l("PropertyName", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 : $x2"
  }
}

case class PropertyDefinition3(x0: MethodDefinition, parserParams: List[Boolean], span: Span) extends PropertyDefinition {
  x0.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("MethodDefinition", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PropertyDefinition4(x1: AssignmentExpression, parserParams: List[Boolean], span: Span) extends PropertyDefinition {
  x1.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("AssignmentExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"... $x1"
  }
}
