package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait CoverParenthesizedExpressionAndArrowParameterList extends AST { val kind: String = "CoverParenthesizedExpressionAndArrowParameterList" }

object CoverParenthesizedExpressionAndArrowParameterList {
  def apply(data: Json): CoverParenthesizedExpressionAndArrowParameterList = AST(data) match {
    case Some(compressed) => CoverParenthesizedExpressionAndArrowParameterList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): CoverParenthesizedExpressionAndArrowParameterList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Expression(_)).get
        CoverParenthesizedExpressionAndArrowParameterList0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(Expression(_)).get
        CoverParenthesizedExpressionAndArrowParameterList1(x0, params, span)
      case 2 =>
        CoverParenthesizedExpressionAndArrowParameterList2(params, span)
      case 3 =>
        val x0 = subs(0).map(BindingIdentifier(_)).get
        CoverParenthesizedExpressionAndArrowParameterList3(x0, params, span)
      case 4 =>
        val x0 = subs(0).map(BindingPattern(_)).get
        CoverParenthesizedExpressionAndArrowParameterList4(x0, params, span)
      case 5 =>
        val x0 = subs(0).map(Expression(_)).get
        val x1 = subs(1).map(BindingIdentifier(_)).get
        CoverParenthesizedExpressionAndArrowParameterList5(x0, x1, params, span)
      case 6 =>
        val x0 = subs(0).map(Expression(_)).get
        val x1 = subs(1).map(BindingPattern(_)).get
        CoverParenthesizedExpressionAndArrowParameterList6(x0, x1, params, span)
    }
  }
}

case class CoverParenthesizedExpressionAndArrowParameterList0(x1: Expression, parserParams: List[Boolean], span: Span) extends CoverParenthesizedExpressionAndArrowParameterList {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Expression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( $x1 )"
  }
}

case class CoverParenthesizedExpressionAndArrowParameterList1(x1: Expression, parserParams: List[Boolean], span: Span) extends CoverParenthesizedExpressionAndArrowParameterList {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Expression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( $x1 , )"
  }
}

case class CoverParenthesizedExpressionAndArrowParameterList2(parserParams: List[Boolean], span: Span) extends CoverParenthesizedExpressionAndArrowParameterList {
  def idx: Int = 2
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( )"
  }
}

case class CoverParenthesizedExpressionAndArrowParameterList3(x2: BindingIdentifier, parserParams: List[Boolean], span: Span) extends CoverParenthesizedExpressionAndArrowParameterList {
  x2.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("BindingIdentifier", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( ... $x2 )"
  }
}

case class CoverParenthesizedExpressionAndArrowParameterList4(x2: BindingPattern, parserParams: List[Boolean], span: Span) extends CoverParenthesizedExpressionAndArrowParameterList {
  x2.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("BindingPattern", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( ... $x2 )"
  }
}

case class CoverParenthesizedExpressionAndArrowParameterList5(x1: Expression, x4: BindingIdentifier, parserParams: List[Boolean], span: Span) extends CoverParenthesizedExpressionAndArrowParameterList {
  x1.parent = Some(this)
  x4.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x4, d(x1, 0))
  def fullList: List[(String, Value)] = l("BindingIdentifier", x4, l("Expression", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( $x1 , ... $x4 )"
  }
}

case class CoverParenthesizedExpressionAndArrowParameterList6(x1: Expression, x4: BindingPattern, parserParams: List[Boolean], span: Span) extends CoverParenthesizedExpressionAndArrowParameterList {
  x1.parent = Some(this)
  x4.parent = Some(this)
  def idx: Int = 6
  def k: Int = d(x4, d(x1, 0))
  def fullList: List[(String, Value)] = l("BindingPattern", x4, l("Expression", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( $x1 , ... $x4 )"
  }
}
