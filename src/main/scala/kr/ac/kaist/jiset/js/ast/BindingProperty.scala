package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait BindingProperty extends AST { val kind: String = "BindingProperty" }

object BindingProperty {
  def apply(data: Json): BindingProperty = AST(data) match {
    case Some(compressed) => BindingProperty(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): BindingProperty = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(SingleNameBinding(_)).get
        BindingProperty0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(PropertyName(_)).get
        val x1 = subs(1).map(BindingElement(_)).get
        BindingProperty1(x0, x1, params, span)
    }
  }
}

case class BindingProperty0(x0: SingleNameBinding, parserParams: List[Boolean], span: Span) extends BindingProperty {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("SingleNameBinding", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BindingProperty1(x0: PropertyName, x2: BindingElement, parserParams: List[Boolean], span: Span) extends BindingProperty {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("BindingElement", x2, l("PropertyName", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 : $x2"
  }
}
