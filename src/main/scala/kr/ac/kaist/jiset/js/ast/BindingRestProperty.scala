package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait BindingRestProperty extends AST { val kind: String = "BindingRestProperty" }

object BindingRestProperty {
  def apply(data: Json): BindingRestProperty = AST(data) match {
    case Some(compressed) => BindingRestProperty(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): BindingRestProperty = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingIdentifier(_)).get
        BindingRestProperty0(x0, params, span)
    }
  }
}

case class BindingRestProperty0(x1: BindingIdentifier, parserParams: List[Boolean], span: Span) extends BindingRestProperty {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("BindingIdentifier", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"... $x1"
  }
}
