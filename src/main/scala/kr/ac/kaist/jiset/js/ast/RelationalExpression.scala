package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait RelationalExpression extends AST { val kind: String = "RelationalExpression" }

object RelationalExpression {
  def apply(data: Json): RelationalExpression = AST(data) match {
    case Some(compressed) => RelationalExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): RelationalExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ShiftExpression(_)).get
        RelationalExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(RelationalExpression(_)).get
        val x1 = subs(1).map(ShiftExpression(_)).get
        RelationalExpression1(x0, x1, params, span)
      case 2 =>
        val x0 = subs(0).map(RelationalExpression(_)).get
        val x1 = subs(1).map(ShiftExpression(_)).get
        RelationalExpression2(x0, x1, params, span)
      case 3 =>
        val x0 = subs(0).map(RelationalExpression(_)).get
        val x1 = subs(1).map(ShiftExpression(_)).get
        RelationalExpression3(x0, x1, params, span)
      case 4 =>
        val x0 = subs(0).map(RelationalExpression(_)).get
        val x1 = subs(1).map(ShiftExpression(_)).get
        RelationalExpression4(x0, x1, params, span)
      case 5 =>
        val x0 = subs(0).map(RelationalExpression(_)).get
        val x1 = subs(1).map(ShiftExpression(_)).get
        RelationalExpression5(x0, x1, params, span)
      case 6 =>
        val x0 = subs(0).map(RelationalExpression(_)).get
        val x1 = subs(1).map(ShiftExpression(_)).get
        RelationalExpression6(x0, x1, params, span)
    }
  }
}

case class RelationalExpression0(x0: ShiftExpression, parserParams: List[Boolean], span: Span) extends RelationalExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ShiftExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class RelationalExpression1(x0: RelationalExpression, x2: ShiftExpression, parserParams: List[Boolean], span: Span) extends RelationalExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("ShiftExpression", x2, l("RelationalExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 < $x2"
  }
}

case class RelationalExpression2(x0: RelationalExpression, x2: ShiftExpression, parserParams: List[Boolean], span: Span) extends RelationalExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("ShiftExpression", x2, l("RelationalExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 > $x2"
  }
}

case class RelationalExpression3(x0: RelationalExpression, x2: ShiftExpression, parserParams: List[Boolean], span: Span) extends RelationalExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("ShiftExpression", x2, l("RelationalExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 <= $x2"
  }
}

case class RelationalExpression4(x0: RelationalExpression, x2: ShiftExpression, parserParams: List[Boolean], span: Span) extends RelationalExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("ShiftExpression", x2, l("RelationalExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 >= $x2"
  }
}

case class RelationalExpression5(x0: RelationalExpression, x2: ShiftExpression, parserParams: List[Boolean], span: Span) extends RelationalExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("ShiftExpression", x2, l("RelationalExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 instanceof $x2"
  }
}

case class RelationalExpression6(x0: RelationalExpression, x2: ShiftExpression, parserParams: List[Boolean], span: Span) extends RelationalExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 6
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("ShiftExpression", x2, l("RelationalExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 in $x2"
  }
}
