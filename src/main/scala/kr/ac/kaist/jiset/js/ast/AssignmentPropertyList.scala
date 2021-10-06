package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AssignmentPropertyList extends AST { val kind: String = "AssignmentPropertyList" }

object AbsAssignmentPropertyList extends AssignmentPropertyList with AbsAST

object AssignmentPropertyList {
  def apply(data: Json): AssignmentPropertyList = AST(data) match {
    case Some(compressed) => AssignmentPropertyList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AssignmentPropertyList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(AssignmentProperty(_)).get
        AssignmentPropertyList0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(AssignmentPropertyList(_)).get
        val x1 = subs(1).map(AssignmentProperty(_)).get
        AssignmentPropertyList1(x0, x1, params, span)
    }
  }
}

case class AssignmentPropertyList0(x0: AssignmentProperty, parserParams: List[Boolean], span: Span) extends AssignmentPropertyList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("AssignmentProperty", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class AssignmentPropertyList1(x0: AssignmentPropertyList, x2: AssignmentProperty, parserParams: List[Boolean], span: Span) extends AssignmentPropertyList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("AssignmentProperty", x2, l("AssignmentPropertyList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
