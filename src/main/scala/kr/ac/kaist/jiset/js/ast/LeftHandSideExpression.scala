package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait LeftHandSideExpression extends AST { val kind: String = "LeftHandSideExpression" }

object AbsLeftHandSideExpression extends LeftHandSideExpression with AbsAST

object LeftHandSideExpression {
  def apply(data: Json): LeftHandSideExpression = AST(data) match {
    case Some(compressed) => LeftHandSideExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): LeftHandSideExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(NewExpression(_)).get
        LeftHandSideExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(CallExpression(_)).get
        LeftHandSideExpression1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(OptionalExpression(_)).get
        LeftHandSideExpression2(x0, params, span)
    }
  }
}

case class LeftHandSideExpression0(x0: NewExpression, parserParams: List[Boolean], span: Span) extends LeftHandSideExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("NewExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class LeftHandSideExpression1(x0: CallExpression, parserParams: List[Boolean], span: Span) extends LeftHandSideExpression {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("CallExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class LeftHandSideExpression2(x0: OptionalExpression, parserParams: List[Boolean], span: Span) extends LeftHandSideExpression {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("OptionalExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
