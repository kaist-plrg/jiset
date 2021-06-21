package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ElementList extends AST { val kind: String = "ElementList" }

case class ElementList0(x0: Option[Elision], x1: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ElementList {
  x0.foreach((m) => m.parent = Some(this))
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("AssignmentExpression", x1, l("Option[Elision]", x0, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"${x0.getOrElse("")} $x1"
  }
}

case class ElementList1(x0: Option[Elision], x1: SpreadElement, parserParams: List[Boolean], span: Span) extends ElementList {
  x0.foreach((m) => m.parent = Some(this))
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("SpreadElement", x1, l("Option[Elision]", x0, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"${x0.getOrElse("")} $x1"
  }
}

case class ElementList2(x0: ElementList, x2: Option[Elision], x3: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ElementList {
  x0.parent = Some(this)
  x2.foreach((m) => m.parent = Some(this))
  x3.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x3, d(x2, d(x0, 0)))
  def fullList: List[(String, Value)] = l("AssignmentExpression", x3, l("Option[Elision]", x2, l("ElementList", x0, Nil))).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"$x0 , ${x2.getOrElse("")} $x3"
  }
}

case class ElementList3(x0: ElementList, x2: Option[Elision], x3: SpreadElement, parserParams: List[Boolean], span: Span) extends ElementList {
  x0.parent = Some(this)
  x2.foreach((m) => m.parent = Some(this))
  x3.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x3, d(x2, d(x0, 0)))
  def fullList: List[(String, Value)] = l("SpreadElement", x3, l("Option[Elision]", x2, l("ElementList", x0, Nil))).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"$x0 , ${x2.getOrElse("")} $x3"
  }
}
