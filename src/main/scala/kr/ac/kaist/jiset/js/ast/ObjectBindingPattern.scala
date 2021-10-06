package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ObjectBindingPattern extends AST { val kind: String = "ObjectBindingPattern" }

object AbsObjectBindingPattern extends ObjectBindingPattern with AbsAST

object ObjectBindingPattern {
  def apply(data: Json): ObjectBindingPattern = AST(data) match {
    case Some(compressed) => ObjectBindingPattern(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ObjectBindingPattern = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        ObjectBindingPattern0(params, span)
      case 1 =>
        val x0 = subs(0).map(BindingRestProperty(_)).get
        ObjectBindingPattern1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(BindingPropertyList(_)).get
        ObjectBindingPattern2(x0, params, span)
      case 3 =>
        val x0 = subs(0).map(BindingPropertyList(_)).get
        val x1 = subs(1).map(BindingRestProperty(_))
        ObjectBindingPattern3(x0, x1, params, span)
    }
  }
}

case class ObjectBindingPattern0(parserParams: List[Boolean], span: Span) extends ObjectBindingPattern {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ }"
  }
}

case class ObjectBindingPattern1(x1: BindingRestProperty, parserParams: List[Boolean], span: Span) extends ObjectBindingPattern {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("BindingRestProperty", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}

case class ObjectBindingPattern2(x1: BindingPropertyList, parserParams: List[Boolean], span: Span) extends ObjectBindingPattern {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("BindingPropertyList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}

case class ObjectBindingPattern3(x1: BindingPropertyList, x3: Option[BindingRestProperty], parserParams: List[Boolean], span: Span) extends ObjectBindingPattern {
  x1.parent = Some(this)
  x3.foreach((m) => m.parent = Some(this))
  def idx: Int = 3
  def k: Int = d(x3, d(x1, 0))
  def fullList: List[(String, PureValue)] = l("Option[BindingRestProperty]", x3, l("BindingPropertyList", x1, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"{ $x1 , ${x3.getOrElse("")} }"
  }
}
