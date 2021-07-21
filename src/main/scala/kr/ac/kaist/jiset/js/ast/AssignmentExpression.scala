package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AssignmentExpression extends AST { val kind: String = "AssignmentExpression" }

object AssignmentExpression {
  def apply(data: Json): AssignmentExpression = AST(data) match {
    case Some(compressed) => AssignmentExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AssignmentExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ConditionalExpression(_)).get
        AssignmentExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(YieldExpression(_)).get
        AssignmentExpression1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(ArrowFunction(_)).get
        AssignmentExpression2(x0, params, span)
      case 3 =>
        val x0 = subs(0).map(AsyncArrowFunction(_)).get
        AssignmentExpression3(x0, params, span)
      case 4 =>
        val x0 = subs(0).map(LeftHandSideExpression(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        AssignmentExpression4(x0, x1, params, span)
      case 5 =>
        val x0 = subs(0).map(LeftHandSideExpression(_)).get
        val x1 = subs(1).map(AssignmentOperator(_)).get
        val x2 = subs(2).map(AssignmentExpression(_)).get
        AssignmentExpression5(x0, x1, x2, params, span)
      case 6 =>
        val x0 = subs(0).map(LeftHandSideExpression(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        AssignmentExpression6(x0, x1, params, span)
      case 7 =>
        val x0 = subs(0).map(LeftHandSideExpression(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        AssignmentExpression7(x0, x1, params, span)
      case 8 =>
        val x0 = subs(0).map(LeftHandSideExpression(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        AssignmentExpression8(x0, x1, params, span)
    }
  }
}

case class AssignmentExpression0(x0: ConditionalExpression, parserParams: List[Boolean], span: Span) extends AssignmentExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ConditionalExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class AssignmentExpression1(x0: YieldExpression, parserParams: List[Boolean], span: Span) extends AssignmentExpression {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("YieldExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class AssignmentExpression2(x0: ArrowFunction, parserParams: List[Boolean], span: Span) extends AssignmentExpression {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ArrowFunction", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class AssignmentExpression3(x0: AsyncArrowFunction, parserParams: List[Boolean], span: Span) extends AssignmentExpression {
  x0.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("AsyncArrowFunction", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class AssignmentExpression4(x0: LeftHandSideExpression, x2: AssignmentExpression, parserParams: List[Boolean], span: Span) extends AssignmentExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("AssignmentExpression", x2, l("LeftHandSideExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 = $x2"
  }
}

case class AssignmentExpression5(x0: LeftHandSideExpression, x1: AssignmentOperator, x2: AssignmentExpression, parserParams: List[Boolean], span: Span) extends AssignmentExpression {
  x0.parent = Some(this)
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x2, d(x1, d(x0, 0)))
  def fullList: List[(String, Value)] = l("AssignmentExpression", x2, l("AssignmentOperator", x1, l("LeftHandSideExpression", x0, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1 $x2"
  }
}

case class AssignmentExpression6(x0: LeftHandSideExpression, x2: AssignmentExpression, parserParams: List[Boolean], span: Span) extends AssignmentExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 6
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("AssignmentExpression", x2, l("LeftHandSideExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 &&= $x2"
  }
}

case class AssignmentExpression7(x0: LeftHandSideExpression, x2: AssignmentExpression, parserParams: List[Boolean], span: Span) extends AssignmentExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 7
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("AssignmentExpression", x2, l("LeftHandSideExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ||= $x2"
  }
}

case class AssignmentExpression8(x0: LeftHandSideExpression, x2: AssignmentExpression, parserParams: List[Boolean], span: Span) extends AssignmentExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 8
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("AssignmentExpression", x2, l("LeftHandSideExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ??= $x2"
  }
}
