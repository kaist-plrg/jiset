package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ObjectAssignmentPattern extends AST { val kind: String = "ObjectAssignmentPattern" }

object AbsObjectAssignmentPattern extends ObjectAssignmentPattern with AbsAST

object ObjectAssignmentPattern {
  def apply(data: Json): ObjectAssignmentPattern = AST(data) match {
    case Some(compressed) => ObjectAssignmentPattern(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ObjectAssignmentPattern = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        ObjectAssignmentPattern0(params, span)
      case 1 =>
        val x0 = subs(0).map(AssignmentRestProperty(_)).get
        ObjectAssignmentPattern1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(AssignmentPropertyList(_)).get
        ObjectAssignmentPattern2(x0, params, span)
      case 3 =>
        val x0 = subs(0).map(AssignmentPropertyList(_)).get
        val x1 = subs(1).map(AssignmentRestProperty(_))
        ObjectAssignmentPattern3(x0, x1, params, span)
    }
  }
}

case class ObjectAssignmentPattern0(parserParams: List[Boolean], span: Span) extends ObjectAssignmentPattern {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ }"
  }
}

case class ObjectAssignmentPattern1(x1: AssignmentRestProperty, parserParams: List[Boolean], span: Span) extends ObjectAssignmentPattern {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("AssignmentRestProperty", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}

case class ObjectAssignmentPattern2(x1: AssignmentPropertyList, parserParams: List[Boolean], span: Span) extends ObjectAssignmentPattern {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("AssignmentPropertyList", x1, Nil).reverse
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
  def fullList: List[(String, PureValue)] = l("Option[AssignmentRestProperty]", x3, l("AssignmentPropertyList", x1, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"{ $x1 , ${x3.getOrElse("")} }"
  }
}
