package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait LogicalANDExpression extends AST { val kind: String = "LogicalANDExpression" }

object AbsLogicalANDExpression extends LogicalANDExpression with AbsAST

object LogicalANDExpression {
  def apply(data: Json): LogicalANDExpression = AST(data) match {
    case Some(compressed) => LogicalANDExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): LogicalANDExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BitwiseORExpression(_)).get
        LogicalANDExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(LogicalANDExpression(_)).get
        val x1 = subs(1).map(BitwiseORExpression(_)).get
        LogicalANDExpression1(x0, x1, params, span)
    }
  }
}

case class LogicalANDExpression0(x0: BitwiseORExpression, parserParams: List[Boolean], span: Span) extends LogicalANDExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("BitwiseORExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class LogicalANDExpression1(x0: LogicalANDExpression, x2: BitwiseORExpression, parserParams: List[Boolean], span: Span) extends LogicalANDExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("BitwiseORExpression", x2, l("LogicalANDExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 && $x2"
  }
}
