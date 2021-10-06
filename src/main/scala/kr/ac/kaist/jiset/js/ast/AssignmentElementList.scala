package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AssignmentElementList extends AST { val kind: String = "AssignmentElementList" }

object AbsAssignmentElementList extends AssignmentElementList with AbsAST

object AssignmentElementList {
  def apply(data: Json): AssignmentElementList = AST(data) match {
    case Some(compressed) => AssignmentElementList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AssignmentElementList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(AssignmentElisionElement(_)).get
        AssignmentElementList0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(AssignmentElementList(_)).get
        val x1 = subs(1).map(AssignmentElisionElement(_)).get
        AssignmentElementList1(x0, x1, params, span)
    }
  }
}

case class AssignmentElementList0(x0: AssignmentElisionElement, parserParams: List[Boolean], span: Span) extends AssignmentElementList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("AssignmentElisionElement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class AssignmentElementList1(x0: AssignmentElementList, x2: AssignmentElisionElement, parserParams: List[Boolean], span: Span) extends AssignmentElementList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("AssignmentElisionElement", x2, l("AssignmentElementList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
