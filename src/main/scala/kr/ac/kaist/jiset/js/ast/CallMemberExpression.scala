package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait CallMemberExpression extends AST { val kind: String = "CallMemberExpression" }

object CallMemberExpression {
  def apply(data: Json): CallMemberExpression = AST(data) match {
    case Some(compressed) => CallMemberExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): CallMemberExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(MemberExpression(_)).get
        val x1 = subs(1).map(Arguments(_)).get
        CallMemberExpression0(x0, x1, params, span)
    }
  }
}

case class CallMemberExpression0(x0: MemberExpression, x1: Arguments, parserParams: List[Boolean], span: Span) extends CallMemberExpression {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("Arguments", x1, l("MemberExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
