package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AdditiveExpression extends AST { val kind: String = "AdditiveExpression" }

object AdditiveExpression {
  def apply(data: Json): AdditiveExpression = AST(data) match {
    case Some(compressed) => AdditiveExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AdditiveExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(MultiplicativeExpression(_)).get
        AdditiveExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(AdditiveExpression(_)).get
        val x1 = subs(1).map(MultiplicativeExpression(_)).get
        AdditiveExpression1(x0, x1, params, span)
      case 2 =>
        val x0 = subs(0).map(AdditiveExpression(_)).get
        val x1 = subs(1).map(MultiplicativeExpression(_)).get
        AdditiveExpression2(x0, x1, params, span)
    }
  }
}

case class AdditiveExpression0(x0: MultiplicativeExpression, parserParams: List[Boolean], span: Span) extends AdditiveExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("MultiplicativeExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class AdditiveExpression1(x0: AdditiveExpression, x2: MultiplicativeExpression, parserParams: List[Boolean], span: Span) extends AdditiveExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("MultiplicativeExpression", x2, l("AdditiveExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 + $x2"
  }
}

case class AdditiveExpression2(x0: AdditiveExpression, x2: MultiplicativeExpression, parserParams: List[Boolean], span: Span) extends AdditiveExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("MultiplicativeExpression", x2, l("AdditiveExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 - $x2"
  }
}
