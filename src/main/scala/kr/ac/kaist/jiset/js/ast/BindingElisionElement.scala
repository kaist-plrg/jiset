package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait BindingElisionElement extends AST { val kind: String = "BindingElisionElement" }

object BindingElisionElement {
  def apply(data: Json): BindingElisionElement = AST(data) match {
    case Some(compressed) => BindingElisionElement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): BindingElisionElement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Elision(_))
        val x1 = subs(1).map(BindingElement(_)).get
        BindingElisionElement0(x0, x1, params, span)
    }
  }
}

case class BindingElisionElement0(x0: Option[Elision], x1: BindingElement, parserParams: List[Boolean], span: Span) extends BindingElisionElement {
  x0.foreach((m) => m.parent = Some(this))
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("BindingElement", x1, l("Option[Elision]", x0, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"${x0.getOrElse("")} $x1"
  }
}
