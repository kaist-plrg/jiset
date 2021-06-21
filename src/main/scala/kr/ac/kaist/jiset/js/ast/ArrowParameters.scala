package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ArrowParameters extends AST { val kind: String = "ArrowParameters" }

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
