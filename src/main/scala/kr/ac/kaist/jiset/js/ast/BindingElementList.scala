package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait BindingElementList extends AST { val kind: String = "BindingElementList" }

object AbsBindingElementList extends BindingElementList with AbsAST

object BindingElementList {
  def apply(data: Json): BindingElementList = AST(data) match {
    case Some(compressed) => BindingElementList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): BindingElementList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingElisionElement(_)).get
        BindingElementList0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(BindingElementList(_)).get
        val x1 = subs(1).map(BindingElisionElement(_)).get
        BindingElementList1(x0, x1, params, span)
    }
  }
}

case class BindingElementList0(x0: BindingElisionElement, parserParams: List[Boolean], span: Span) extends BindingElementList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("BindingElisionElement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BindingElementList1(x0: BindingElementList, x2: BindingElisionElement, parserParams: List[Boolean], span: Span) extends BindingElementList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("BindingElisionElement", x2, l("BindingElementList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
