package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ConciseBody extends AST { val kind: String = "ConciseBody" }

object ConciseBody {
  def apply(data: Json): ConciseBody = AST(data) match {
    case Some(compressed) => ConciseBody(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ConciseBody = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ExpressionBody(_)).get
        ConciseBody0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(FunctionBody(_)).get
        ConciseBody1(x0, params, span)
    }
  }
}

case class ConciseBody0(x1: ExpressionBody, parserParams: List[Boolean], span: Span) extends ConciseBody {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("ExpressionBody", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x1"
  }
}

case class ConciseBody1(x1: FunctionBody, parserParams: List[Boolean], span: Span) extends ConciseBody {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("FunctionBody", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}
