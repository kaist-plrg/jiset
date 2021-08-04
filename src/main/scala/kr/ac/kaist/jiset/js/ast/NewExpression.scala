package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait NewExpression extends AST { val kind: String = "NewExpression" }

object NewExpression {
  def apply(data: Json): NewExpression = AST(data) match {
    case Some(compressed) => NewExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): NewExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(MemberExpression(_)).get
        NewExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(NewExpression(_)).get
        NewExpression1(x0, params, span)
    }
  }
}

case class NewExpression0(x0: MemberExpression, parserParams: List[Boolean], span: Span) extends NewExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("MemberExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class NewExpression1(x1: NewExpression, parserParams: List[Boolean], span: Span) extends NewExpression {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("NewExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"new $x1"
  }
}
