package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AsyncArrowFunction extends AST { val kind: String = "AsyncArrowFunction" }

object AsyncArrowFunction {
  def apply(data: Json): AsyncArrowFunction = AST(data) match {
    case Some(compressed) => AsyncArrowFunction(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AsyncArrowFunction = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(AsyncArrowBindingIdentifier(_)).get
        val x1 = subs(1).map(AsyncConciseBody(_)).get
        AsyncArrowFunction0(x0, x1, params, span)
      case 1 =>
        val x0 = subs(0).map(CoverCallExpressionAndAsyncArrowHead(_)).get
        val x1 = subs(1).map(AsyncConciseBody(_)).get
        AsyncArrowFunction1(x0, x1, params, span)
    }
  }
}

case class AsyncArrowFunction0(x2: AsyncArrowBindingIdentifier, x5: AsyncConciseBody, parserParams: List[Boolean], span: Span) extends AsyncArrowFunction {
  x2.parent = Some(this)
  x5.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x5, d(x2, 0))
  def fullList: List[(String, PureValue)] = l("AsyncConciseBody", x5, l("AsyncArrowBindingIdentifier", x2, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"async $x2 => $x5"
  }
}

case class AsyncArrowFunction1(x0: CoverCallExpressionAndAsyncArrowHead, x3: AsyncConciseBody, parserParams: List[Boolean], span: Span) extends AsyncArrowFunction {
  x0.parent = Some(this)
  x3.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x3, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("AsyncConciseBody", x3, l("CoverCallExpressionAndAsyncArrowHead", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 => $x3"
  }
}
