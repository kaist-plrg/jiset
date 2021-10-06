package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ConditionalExpression extends AST { val kind: String = "ConditionalExpression" }

object AbsConditionalExpression extends ConditionalExpression with AbsAST

object ConditionalExpression {
  def apply(data: Json): ConditionalExpression = AST(data) match {
    case Some(compressed) => ConditionalExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ConditionalExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ShortCircuitExpression(_)).get
        ConditionalExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(ShortCircuitExpression(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        val x2 = subs(2).map(AssignmentExpression(_)).get
        ConditionalExpression1(x0, x1, x2, params, span)
    }
  }
}

case class ConditionalExpression0(x0: ShortCircuitExpression, parserParams: List[Boolean], span: Span) extends ConditionalExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ShortCircuitExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ConditionalExpression1(x0: ShortCircuitExpression, x2: AssignmentExpression, x4: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ConditionalExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  x4.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x4, d(x2, d(x0, 0)))
  def fullList: List[(String, PureValue)] = l("AssignmentExpression1", x4, l("AssignmentExpression0", x2, l("ShortCircuitExpression", x0, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ? $x2 : $x4"
  }
}
