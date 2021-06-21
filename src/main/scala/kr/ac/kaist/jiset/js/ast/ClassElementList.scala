package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ClassElementList extends AST { val kind: String = "ClassElementList" }

case class ClassElementList0(x0: ClassElement, parserParams: List[Boolean], span: Span) extends ClassElementList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ClassElement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ClassElementList1(x0: ClassElementList, x1: ClassElement, parserParams: List[Boolean], span: Span) extends ClassElementList {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("ClassElement", x1, l("ClassElementList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
