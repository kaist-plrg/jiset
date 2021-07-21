package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AssignmentProperty extends AST { val kind: String = "AssignmentProperty" }

object AssignmentProperty {
  def apply(data: Json): AssignmentProperty = AST(data) match {
    case Some(compressed) => AssignmentProperty(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AssignmentProperty = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(IdentifierReference(_)).get
        val x1 = subs(1).map(Initializer(_))
        AssignmentProperty0(x0, x1, params, span)
      case 1 =>
        val x0 = subs(0).map(PropertyName(_)).get
        val x1 = subs(1).map(AssignmentElement(_)).get
        AssignmentProperty1(x0, x1, params, span)
    }
  }
}

case class AssignmentProperty0(x0: IdentifierReference, x1: Option[Initializer], parserParams: List[Boolean], span: Span) extends AssignmentProperty {
  x0.parent = Some(this)
  x1.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("Option[Initializer]", x1, l("IdentifierReference", x0, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"$x0 ${x1.getOrElse("")}"
  }
}

case class AssignmentProperty1(x0: PropertyName, x2: AssignmentElement, parserParams: List[Boolean], span: Span) extends AssignmentProperty {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("AssignmentElement", x2, l("PropertyName", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 : $x2"
  }
}
