package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AssignmentPattern extends AST { val kind: String = "AssignmentPattern" }

object AbsAssignmentPattern extends AssignmentPattern with AbsAST

object AssignmentPattern {
  def apply(data: Json): AssignmentPattern = AST(data) match {
    case Some(compressed) => AssignmentPattern(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AssignmentPattern = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ObjectAssignmentPattern(_)).get
        AssignmentPattern0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(ArrayAssignmentPattern(_)).get
        AssignmentPattern1(x0, params, span)
    }
  }
}

case class AssignmentPattern0(x0: ObjectAssignmentPattern, parserParams: List[Boolean], span: Span) extends AssignmentPattern {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ObjectAssignmentPattern", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class AssignmentPattern1(x0: ArrayAssignmentPattern, parserParams: List[Boolean], span: Span) extends AssignmentPattern {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ArrayAssignmentPattern", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
