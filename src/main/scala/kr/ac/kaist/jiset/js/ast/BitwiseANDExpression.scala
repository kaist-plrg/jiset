package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait BitwiseANDExpression extends AST { val kind: String = "BitwiseANDExpression" }

object AbsBitwiseANDExpression extends BitwiseANDExpression with AbsAST

object BitwiseANDExpression {
  def apply(data: Json): BitwiseANDExpression = AST(data) match {
    case Some(compressed) => BitwiseANDExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): BitwiseANDExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(EqualityExpression(_)).get
        BitwiseANDExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(BitwiseANDExpression(_)).get
        val x1 = subs(1).map(EqualityExpression(_)).get
        BitwiseANDExpression1(x0, x1, params, span)
    }
  }
}

case class BitwiseANDExpression0(x0: EqualityExpression, parserParams: List[Boolean], span: Span) extends BitwiseANDExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("EqualityExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BitwiseANDExpression1(x0: BitwiseANDExpression, x2: EqualityExpression, parserParams: List[Boolean], span: Span) extends BitwiseANDExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("EqualityExpression", x2, l("BitwiseANDExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 & $x2"
  }
}
