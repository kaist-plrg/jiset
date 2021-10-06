package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait BindingPropertyList extends AST { val kind: String = "BindingPropertyList" }

object AbsBindingPropertyList extends BindingPropertyList with AbsAST

object BindingPropertyList {
  def apply(data: Json): BindingPropertyList = AST(data) match {
    case Some(compressed) => BindingPropertyList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): BindingPropertyList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingProperty(_)).get
        BindingPropertyList0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(BindingPropertyList(_)).get
        val x1 = subs(1).map(BindingProperty(_)).get
        BindingPropertyList1(x0, x1, params, span)
    }
  }
}

case class BindingPropertyList0(x0: BindingProperty, parserParams: List[Boolean], span: Span) extends BindingPropertyList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("BindingProperty", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BindingPropertyList1(x0: BindingPropertyList, x2: BindingProperty, parserParams: List[Boolean], span: Span) extends BindingPropertyList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("BindingProperty", x2, l("BindingPropertyList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
