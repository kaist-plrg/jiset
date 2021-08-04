package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait OptionalExpression extends AST { val kind: String = "OptionalExpression" }

object OptionalExpression {
  def apply(data: Json): OptionalExpression = AST(data) match {
    case Some(compressed) => OptionalExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): OptionalExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(MemberExpression(_)).get
        val x1 = subs(1).map(OptionalChain(_)).get
        OptionalExpression0(x0, x1, params, span)
      case 1 =>
        val x0 = subs(0).map(CallExpression(_)).get
        val x1 = subs(1).map(OptionalChain(_)).get
        OptionalExpression1(x0, x1, params, span)
      case 2 =>
        val x0 = subs(0).map(OptionalExpression(_)).get
        val x1 = subs(1).map(OptionalChain(_)).get
        OptionalExpression2(x0, x1, params, span)
    }
  }
}

case class OptionalExpression0(x0: MemberExpression, x1: OptionalChain, parserParams: List[Boolean], span: Span) extends OptionalExpression {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("OptionalChain", x1, l("MemberExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}

case class OptionalExpression1(x0: CallExpression, x1: OptionalChain, parserParams: List[Boolean], span: Span) extends OptionalExpression {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("OptionalChain", x1, l("CallExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}

case class OptionalExpression2(x0: OptionalExpression, x1: OptionalChain, parserParams: List[Boolean], span: Span) extends OptionalExpression {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("OptionalChain", x1, l("OptionalExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
