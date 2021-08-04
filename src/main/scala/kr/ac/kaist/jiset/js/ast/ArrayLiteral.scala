package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ArrayLiteral extends AST { val kind: String = "ArrayLiteral" }

object ArrayLiteral {
  def apply(data: Json): ArrayLiteral = AST(data) match {
    case Some(compressed) => ArrayLiteral(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ArrayLiteral = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Elision(_))
        ArrayLiteral0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(ElementList(_)).get
        ArrayLiteral1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(ElementList(_)).get
        val x1 = subs(1).map(Elision(_))
        ArrayLiteral2(x0, x1, params, span)
    }
  }
}

case class ArrayLiteral0(x1: Option[Elision], parserParams: List[Boolean], span: Span) extends ArrayLiteral {
  x1.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("Option[Elision]", x1, Nil).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"[ ${x1.getOrElse("")} ]"
  }
}

case class ArrayLiteral1(x1: ElementList, parserParams: List[Boolean], span: Span) extends ArrayLiteral {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("ElementList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"[ $x1 ]"
  }
}

case class ArrayLiteral2(x1: ElementList, x3: Option[Elision], parserParams: List[Boolean], span: Span) extends ArrayLiteral {
  x1.parent = Some(this)
  x3.foreach((m) => m.parent = Some(this))
  def idx: Int = 2
  def k: Int = d(x3, d(x1, 0))
  def fullList: List[(String, PureValue)] = l("Option[Elision]", x3, l("ElementList", x1, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"[ $x1 , ${x3.getOrElse("")} ]"
  }
}
