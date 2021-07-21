package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait CoalesceExpressionHead extends AST { val kind: String = "CoalesceExpressionHead" }

object CoalesceExpressionHead {
  def apply(data: Json): CoalesceExpressionHead = AST(data) match {
    case Some(compressed) => CoalesceExpressionHead(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): CoalesceExpressionHead = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(CoalesceExpression(_)).get
        CoalesceExpressionHead0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(BitwiseORExpression(_)).get
        CoalesceExpressionHead1(x0, params, span)
    }
  }
}

case class CoalesceExpressionHead0(x0: CoalesceExpression, parserParams: List[Boolean], span: Span) extends CoalesceExpressionHead {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("CoalesceExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class CoalesceExpressionHead1(x0: BitwiseORExpression, parserParams: List[Boolean], span: Span) extends CoalesceExpressionHead {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("BitwiseORExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
