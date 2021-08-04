package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait UnaryExpression extends AST { val kind: String = "UnaryExpression" }

object UnaryExpression {
  def apply(data: Json): UnaryExpression = AST(data) match {
    case Some(compressed) => UnaryExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): UnaryExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(UpdateExpression(_)).get
        UnaryExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(UnaryExpression(_)).get
        UnaryExpression1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(UnaryExpression(_)).get
        UnaryExpression2(x0, params, span)
      case 3 =>
        val x0 = subs(0).map(UnaryExpression(_)).get
        UnaryExpression3(x0, params, span)
      case 4 =>
        val x0 = subs(0).map(UnaryExpression(_)).get
        UnaryExpression4(x0, params, span)
      case 5 =>
        val x0 = subs(0).map(UnaryExpression(_)).get
        UnaryExpression5(x0, params, span)
      case 6 =>
        val x0 = subs(0).map(UnaryExpression(_)).get
        UnaryExpression6(x0, params, span)
      case 7 =>
        val x0 = subs(0).map(UnaryExpression(_)).get
        UnaryExpression7(x0, params, span)
      case 8 =>
        val x0 = subs(0).map(AwaitExpression(_)).get
        UnaryExpression8(x0, params, span)
    }
  }
}

case class UnaryExpression0(x0: UpdateExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("UpdateExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class UnaryExpression1(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"delete $x1"
  }
}

case class UnaryExpression2(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"void $x1"
  }
}

case class UnaryExpression3(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x1.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"typeof $x1"
  }
}

case class UnaryExpression4(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x1.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"+ $x1"
  }
}

case class UnaryExpression5(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x1.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"- $x1"
  }
}

case class UnaryExpression6(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x1.parent = Some(this)
  def idx: Int = 6
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"~ $x1"
  }
}

case class UnaryExpression7(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x1.parent = Some(this)
  def idx: Int = 7
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"! $x1"
  }
}

case class UnaryExpression8(x0: AwaitExpression, parserParams: List[Boolean], span: Span) extends UnaryExpression {
  x0.parent = Some(this)
  def idx: Int = 8
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("AwaitExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
