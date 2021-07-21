package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ExpressionStatement extends AST { val kind: String = "ExpressionStatement" }

object ExpressionStatement {
  def apply(data: Json): ExpressionStatement = AST(data) match {
    case Some(compressed) => ExpressionStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ExpressionStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Expression(_)).get
        ExpressionStatement0(x0, params, span)
    }
  }
}

case class ExpressionStatement0(x1: Expression, parserParams: List[Boolean], span: Span) extends ExpressionStatement {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Expression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x1 ;"
  }
}
