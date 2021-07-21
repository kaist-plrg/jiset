package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait BindingPattern extends AST { val kind: String = "BindingPattern" }

object BindingPattern {
  def apply(data: Json): BindingPattern = AST(data) match {
    case Some(compressed) => BindingPattern(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): BindingPattern = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ObjectBindingPattern(_)).get
        BindingPattern0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(ArrayBindingPattern(_)).get
        BindingPattern1(x0, params, span)
    }
  }
}

case class BindingPattern0(x0: ObjectBindingPattern, parserParams: List[Boolean], span: Span) extends BindingPattern {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ObjectBindingPattern", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BindingPattern1(x0: ArrayBindingPattern, parserParams: List[Boolean], span: Span) extends BindingPattern {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ArrayBindingPattern", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
