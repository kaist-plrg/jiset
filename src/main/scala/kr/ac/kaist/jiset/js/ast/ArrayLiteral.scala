package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ArrayLiteral extends AST { val kind: String = "ArrayLiteral" }

case class ArrayLiteral0(x1: Option[Elision], parserParams: List[Boolean], span: Span) extends ArrayLiteral {
  x1.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Option[Elision]", x1, Nil).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"[ ${x1.getOrElse("")} ]"
  }
}

case class ArrayLiteral1(x1: ElementList, parserParams: List[Boolean], span: Span) extends ArrayLiteral {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("ElementList", x1, Nil).reverse
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
  def fullList: List[(String, Value)] = l("Option[Elision]", x3, l("ElementList", x1, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"[ $x1 , ${x3.getOrElse("")} ]"
  }
}
