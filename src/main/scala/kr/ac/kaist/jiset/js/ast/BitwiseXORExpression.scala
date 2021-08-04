package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait BitwiseXORExpression extends AST { val kind: String = "BitwiseXORExpression" }

object BitwiseXORExpression {
  def apply(data: Json): BitwiseXORExpression = AST(data) match {
    case Some(compressed) => BitwiseXORExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): BitwiseXORExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BitwiseANDExpression(_)).get
        BitwiseXORExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(BitwiseXORExpression(_)).get
        val x1 = subs(1).map(BitwiseANDExpression(_)).get
        BitwiseXORExpression1(x0, x1, params, span)
    }
  }
}

case class BitwiseXORExpression0(x0: BitwiseANDExpression, parserParams: List[Boolean], span: Span) extends BitwiseXORExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("BitwiseANDExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BitwiseXORExpression1(x0: BitwiseXORExpression, x2: BitwiseANDExpression, parserParams: List[Boolean], span: Span) extends BitwiseXORExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("BitwiseANDExpression", x2, l("BitwiseXORExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ^ $x2"
  }
}
