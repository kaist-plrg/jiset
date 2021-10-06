package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait UpdateExpression extends AST { val kind: String = "UpdateExpression" }

object AbsUpdateExpression extends UpdateExpression with AbsAST

object UpdateExpression {
  def apply(data: Json): UpdateExpression = AST(data) match {
    case Some(compressed) => UpdateExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): UpdateExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(LeftHandSideExpression(_)).get
        UpdateExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(LeftHandSideExpression(_)).get
        UpdateExpression1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(LeftHandSideExpression(_)).get
        UpdateExpression2(x0, params, span)
      case 3 =>
        val x0 = subs(0).map(UnaryExpression(_)).get
        UpdateExpression3(x0, params, span)
      case 4 =>
        val x0 = subs(0).map(UnaryExpression(_)).get
        UpdateExpression4(x0, params, span)
    }
  }
}

case class UpdateExpression0(x0: LeftHandSideExpression, parserParams: List[Boolean], span: Span) extends UpdateExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("LeftHandSideExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class UpdateExpression1(x0: LeftHandSideExpression, parserParams: List[Boolean], span: Span) extends UpdateExpression {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("LeftHandSideExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ++"
  }
}

case class UpdateExpression2(x0: LeftHandSideExpression, parserParams: List[Boolean], span: Span) extends UpdateExpression {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("LeftHandSideExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 --"
  }
}

case class UpdateExpression3(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UpdateExpression {
  x1.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"++ $x1"
  }
}

case class UpdateExpression4(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UpdateExpression {
  x1.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"-- $x1"
  }
}
