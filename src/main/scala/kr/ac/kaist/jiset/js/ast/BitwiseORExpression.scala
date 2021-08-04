package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait BitwiseORExpression extends AST { val kind: String = "BitwiseORExpression" }

object BitwiseORExpression {
  def apply(data: Json): BitwiseORExpression = AST(data) match {
    case Some(compressed) => BitwiseORExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): BitwiseORExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BitwiseXORExpression(_)).get
        BitwiseORExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(BitwiseORExpression(_)).get
        val x1 = subs(1).map(BitwiseXORExpression(_)).get
        BitwiseORExpression1(x0, x1, params, span)
    }
  }
}

case class BitwiseORExpression0(x0: BitwiseXORExpression, parserParams: List[Boolean], span: Span) extends BitwiseORExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("BitwiseXORExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BitwiseORExpression1(x0: BitwiseORExpression, x2: BitwiseXORExpression, parserParams: List[Boolean], span: Span) extends BitwiseORExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("BitwiseXORExpression", x2, l("BitwiseORExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 | $x2"
  }
}
