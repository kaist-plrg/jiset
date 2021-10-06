package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ShiftExpression extends AST { val kind: String = "ShiftExpression" }

object AbsShiftExpression extends ShiftExpression with AbsAST

object ShiftExpression {
  def apply(data: Json): ShiftExpression = AST(data) match {
    case Some(compressed) => ShiftExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ShiftExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(AdditiveExpression(_)).get
        ShiftExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(ShiftExpression(_)).get
        val x1 = subs(1).map(AdditiveExpression(_)).get
        ShiftExpression1(x0, x1, params, span)
      case 2 =>
        val x0 = subs(0).map(ShiftExpression(_)).get
        val x1 = subs(1).map(AdditiveExpression(_)).get
        ShiftExpression2(x0, x1, params, span)
      case 3 =>
        val x0 = subs(0).map(ShiftExpression(_)).get
        val x1 = subs(1).map(AdditiveExpression(_)).get
        ShiftExpression3(x0, x1, params, span)
    }
  }
}

case class ShiftExpression0(x0: AdditiveExpression, parserParams: List[Boolean], span: Span) extends ShiftExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("AdditiveExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ShiftExpression1(x0: ShiftExpression, x2: AdditiveExpression, parserParams: List[Boolean], span: Span) extends ShiftExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("AdditiveExpression", x2, l("ShiftExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 << $x2"
  }
}

case class ShiftExpression2(x0: ShiftExpression, x2: AdditiveExpression, parserParams: List[Boolean], span: Span) extends ShiftExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("AdditiveExpression", x2, l("ShiftExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 >> $x2"
  }
}

case class ShiftExpression3(x0: ShiftExpression, x2: AdditiveExpression, parserParams: List[Boolean], span: Span) extends ShiftExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("AdditiveExpression", x2, l("ShiftExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 >>> $x2"
  }
}
