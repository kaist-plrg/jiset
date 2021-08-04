package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait CoalesceExpression extends AST { val kind: String = "CoalesceExpression" }

object CoalesceExpression {
  def apply(data: Json): CoalesceExpression = AST(data) match {
    case Some(compressed) => CoalesceExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): CoalesceExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(CoalesceExpressionHead(_)).get
        val x1 = subs(1).map(BitwiseORExpression(_)).get
        CoalesceExpression0(x0, x1, params, span)
    }
  }
}

case class CoalesceExpression0(x0: CoalesceExpressionHead, x2: BitwiseORExpression, parserParams: List[Boolean], span: Span) extends CoalesceExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("BitwiseORExpression", x2, l("CoalesceExpressionHead", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ?? $x2"
  }
}
