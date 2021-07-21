package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AsyncConciseBody extends AST { val kind: String = "AsyncConciseBody" }

object AsyncConciseBody {
  def apply(data: Json): AsyncConciseBody = AST(data) match {
    case Some(compressed) => AsyncConciseBody(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AsyncConciseBody = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ExpressionBody(_)).get
        AsyncConciseBody0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(AsyncFunctionBody(_)).get
        AsyncConciseBody1(x0, params, span)
    }
  }
}

case class AsyncConciseBody0(x1: ExpressionBody, parserParams: List[Boolean], span: Span) extends AsyncConciseBody {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("ExpressionBody", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x1"
  }
}

case class AsyncConciseBody1(x1: AsyncFunctionBody, parserParams: List[Boolean], span: Span) extends AsyncConciseBody {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("AsyncFunctionBody", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}
