package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait PropertyName extends AST { val kind: String = "PropertyName" }

object PropertyName {
  def apply(data: Json): PropertyName = AST(data) match {
    case Some(compressed) => PropertyName(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): PropertyName = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(LiteralPropertyName(_)).get
        PropertyName0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(ComputedPropertyName(_)).get
        PropertyName1(x0, params, span)
    }
  }
}

case class PropertyName0(x0: LiteralPropertyName, parserParams: List[Boolean], span: Span) extends PropertyName {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("LiteralPropertyName", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PropertyName1(x0: ComputedPropertyName, parserParams: List[Boolean], span: Span) extends PropertyName {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ComputedPropertyName", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
