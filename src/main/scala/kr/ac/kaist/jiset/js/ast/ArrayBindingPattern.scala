package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ArrayBindingPattern extends AST { val kind: String = "ArrayBindingPattern" }

object AbsArrayBindingPattern extends ArrayBindingPattern with AbsAST

object ArrayBindingPattern {
  def apply(data: Json): ArrayBindingPattern = AST(data) match {
    case Some(compressed) => ArrayBindingPattern(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ArrayBindingPattern = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Elision(_))
        val x1 = subs(1).map(BindingRestElement(_))
        ArrayBindingPattern0(x0, x1, params, span)
      case 1 =>
        val x0 = subs(0).map(BindingElementList(_)).get
        ArrayBindingPattern1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(BindingElementList(_)).get
        val x1 = subs(1).map(Elision(_))
        val x2 = subs(2).map(BindingRestElement(_))
        ArrayBindingPattern2(x0, x1, x2, params, span)
    }
  }
}

case class ArrayBindingPattern0(x1: Option[Elision], x2: Option[BindingRestElement], parserParams: List[Boolean], span: Span) extends ArrayBindingPattern {
  x1.foreach((m) => m.parent = Some(this))
  x2.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, PureValue)] = l("Option[BindingRestElement]", x2, l("Option[Elision]", x1, Nil)).reverse
  def maxK: Int = 3
  override def toString: String = {
    s"[ ${x1.getOrElse("")} ${x2.getOrElse("")} ]"
  }
}

case class ArrayBindingPattern1(x1: BindingElementList, parserParams: List[Boolean], span: Span) extends ArrayBindingPattern {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("BindingElementList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"[ $x1 ]"
  }
}

case class ArrayBindingPattern2(x1: BindingElementList, x3: Option[Elision], x4: Option[BindingRestElement], parserParams: List[Boolean], span: Span) extends ArrayBindingPattern {
  x1.parent = Some(this)
  x3.foreach((m) => m.parent = Some(this))
  x4.foreach((m) => m.parent = Some(this))
  def idx: Int = 2
  def k: Int = d(x4, d(x3, d(x1, 0)))
  def fullList: List[(String, PureValue)] = l("Option[BindingRestElement]", x4, l("Option[Elision]", x3, l("BindingElementList", x1, Nil))).reverse
  def maxK: Int = 3
  override def toString: String = {
    s"[ $x1 , ${x3.getOrElse("")} ${x4.getOrElse("")} ]"
  }
}
