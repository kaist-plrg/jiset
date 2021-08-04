package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait LogicalORExpression extends AST { val kind: String = "LogicalORExpression" }

object LogicalORExpression {
  def apply(data: Json): LogicalORExpression = AST(data) match {
    case Some(compressed) => LogicalORExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): LogicalORExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(LogicalANDExpression(_)).get
        LogicalORExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(LogicalORExpression(_)).get
        val x1 = subs(1).map(LogicalANDExpression(_)).get
        LogicalORExpression1(x0, x1, params, span)
    }
  }
}

case class LogicalORExpression0(x0: LogicalANDExpression, parserParams: List[Boolean], span: Span) extends LogicalORExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("LogicalANDExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class LogicalORExpression1(x0: LogicalORExpression, x2: LogicalANDExpression, parserParams: List[Boolean], span: Span) extends LogicalORExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("LogicalANDExpression", x2, l("LogicalORExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 || $x2"
  }
}
