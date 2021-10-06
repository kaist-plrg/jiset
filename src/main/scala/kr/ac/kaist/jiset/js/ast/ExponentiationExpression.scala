package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ExponentiationExpression extends AST { val kind: String = "ExponentiationExpression" }

object AbsExponentiationExpression extends ExponentiationExpression with AbsAST

object ExponentiationExpression {
  def apply(data: Json): ExponentiationExpression = AST(data) match {
    case Some(compressed) => ExponentiationExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ExponentiationExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(UnaryExpression(_)).get
        ExponentiationExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(UpdateExpression(_)).get
        val x1 = subs(1).map(ExponentiationExpression(_)).get
        ExponentiationExpression1(x0, x1, params, span)
    }
  }
}

case class ExponentiationExpression0(x0: UnaryExpression, parserParams: List[Boolean], span: Span) extends ExponentiationExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("UnaryExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ExponentiationExpression1(x0: UpdateExpression, x2: ExponentiationExpression, parserParams: List[Boolean], span: Span) extends ExponentiationExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("ExponentiationExpression", x2, l("UpdateExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ** $x2"
  }
}
