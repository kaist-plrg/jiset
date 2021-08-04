package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait Expression extends AST { val kind: String = "Expression" }

object Expression {
  def apply(data: Json): Expression = AST(data) match {
    case Some(compressed) => Expression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): Expression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(AssignmentExpression(_)).get
        Expression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(Expression(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        Expression1(x0, x1, params, span)
    }
  }
}

case class Expression0(x0: AssignmentExpression, parserParams: List[Boolean], span: Span) extends Expression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("AssignmentExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Expression1(x0: Expression, x2: AssignmentExpression, parserParams: List[Boolean], span: Span) extends Expression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("AssignmentExpression", x2, l("Expression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
