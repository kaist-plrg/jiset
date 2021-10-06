package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait MultiplicativeExpression extends AST { val kind: String = "MultiplicativeExpression" }

object AbsMultiplicativeExpression extends MultiplicativeExpression with AbsAST

object MultiplicativeExpression {
  def apply(data: Json): MultiplicativeExpression = AST(data) match {
    case Some(compressed) => MultiplicativeExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): MultiplicativeExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ExponentiationExpression(_)).get
        MultiplicativeExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(MultiplicativeExpression(_)).get
        val x1 = subs(1).map(MultiplicativeOperator(_)).get
        val x2 = subs(2).map(ExponentiationExpression(_)).get
        MultiplicativeExpression1(x0, x1, x2, params, span)
    }
  }
}

case class MultiplicativeExpression0(x0: ExponentiationExpression, parserParams: List[Boolean], span: Span) extends MultiplicativeExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ExponentiationExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class MultiplicativeExpression1(x0: MultiplicativeExpression, x1: MultiplicativeOperator, x2: ExponentiationExpression, parserParams: List[Boolean], span: Span) extends MultiplicativeExpression {
  x0.parent = Some(this)
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x1, d(x0, 0)))
  def fullList: List[(String, PureValue)] = l("ExponentiationExpression", x2, l("MultiplicativeOperator", x1, l("MultiplicativeExpression", x0, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1 $x2"
  }
}
