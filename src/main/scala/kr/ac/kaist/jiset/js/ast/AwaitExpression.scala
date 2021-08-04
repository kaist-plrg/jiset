package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AwaitExpression extends AST { val kind: String = "AwaitExpression" }

object AwaitExpression {
  def apply(data: Json): AwaitExpression = AST(data) match {
    case Some(compressed) => AwaitExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AwaitExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(UnaryExpression(_)).get
        AwaitExpression0(x0, params, span)
    }
  }
}

case class AwaitExpression0(x1: UnaryExpression, parserParams: List[Boolean], span: Span) extends AwaitExpression {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("UnaryExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"await $x1"
  }
}
