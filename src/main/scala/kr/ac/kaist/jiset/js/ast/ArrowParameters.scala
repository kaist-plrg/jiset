package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ArrowParameters extends AST { val kind: String = "ArrowParameters" }

object ArrowParameters {
  def apply(data: Json): ArrowParameters = AST(data) match {
    case Some(compressed) => ArrowParameters(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ArrowParameters = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingIdentifier(_)).get
        ArrowParameters0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(CoverParenthesizedExpressionAndArrowParameterList(_)).get
        ArrowParameters1(x0, params, span)
    }
  }
}

case class ArrowParameters0(x0: BindingIdentifier, parserParams: List[Boolean], span: Span) extends ArrowParameters {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("BindingIdentifier", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ArrowParameters1(x0: CoverParenthesizedExpressionAndArrowParameterList, parserParams: List[Boolean], span: Span) extends ArrowParameters {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("CoverParenthesizedExpressionAndArrowParameterList", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
