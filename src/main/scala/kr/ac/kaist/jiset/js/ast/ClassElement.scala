package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ClassElement extends AST { val kind: String = "ClassElement" }

case class ClassElement0(x0: MethodDefinition, parserParams: List[Boolean], span: Span) extends ClassElement {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("MethodDefinition", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ClassElement1(x1: MethodDefinition, parserParams: List[Boolean], span: Span) extends ClassElement {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("MethodDefinition", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"static $x1"
  }
}

case class ClassElement2(parserParams: List[Boolean], span: Span) extends ClassElement {
  def idx: Int = 2
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s";"
  }
}
