package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait BindingElement extends AST { val kind: String = "BindingElement" }

object BindingElement {
  def apply(data: Json): BindingElement = AST(data) match {
    case Some(compressed) => BindingElement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): BindingElement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(SingleNameBinding(_)).get
        BindingElement0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(BindingPattern(_)).get
        val x1 = subs(1).map(Initializer(_))
        BindingElement1(x0, x1, params, span)
    }
  }
}

case class BindingElement0(x0: SingleNameBinding, parserParams: List[Boolean], span: Span) extends BindingElement {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("SingleNameBinding", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BindingElement1(x0: BindingPattern, x1: Option[Initializer], parserParams: List[Boolean], span: Span) extends BindingElement {
  x0.parent = Some(this)
  x1.foreach((m) => m.parent = Some(this))
  def idx: Int = 1
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("Option[Initializer]", x1, l("BindingPattern", x0, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"$x0 ${x1.getOrElse("")}"
  }
}
