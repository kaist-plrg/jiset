package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ExpressionBody extends AST { val kind: String = "ExpressionBody" }

object ExpressionBody {
  def apply(data: Json): ExpressionBody = AST(data) match {
    case Some(compressed) => ExpressionBody(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ExpressionBody = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(AssignmentExpression(_)).get
        ExpressionBody0(x0, params, span)
    }
  }
}

case class ExpressionBody0(x0: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ExpressionBody {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("AssignmentExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
