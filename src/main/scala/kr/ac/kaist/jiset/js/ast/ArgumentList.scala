package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ArgumentList extends AST { val kind: String = "ArgumentList" }

object ArgumentList {
  def apply(data: Json): ArgumentList = AST(data) match {
    case Some(compressed) => ArgumentList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ArgumentList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(AssignmentExpression(_)).get
        ArgumentList0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(AssignmentExpression(_)).get
        ArgumentList1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(ArgumentList(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        ArgumentList2(x0, x1, params, span)
      case 3 =>
        val x0 = subs(0).map(ArgumentList(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        ArgumentList3(x0, x1, params, span)
    }
  }
}

case class ArgumentList0(x0: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ArgumentList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("AssignmentExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ArgumentList1(x1: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ArgumentList {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("AssignmentExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"... $x1"
  }
}

case class ArgumentList2(x0: ArgumentList, x2: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ArgumentList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("AssignmentExpression", x2, l("ArgumentList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}

case class ArgumentList3(x0: ArgumentList, x3: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ArgumentList {
  x0.parent = Some(this)
  x3.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x3, d(x0, 0))
  def fullList: List[(String, Value)] = l("AssignmentExpression", x3, l("ArgumentList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , ... $x3"
  }
}
