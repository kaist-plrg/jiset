package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ClassDeclaration extends AST { val kind: String = "ClassDeclaration" }

case class ClassDeclaration0(x1: BindingIdentifier, x2: ClassTail, parserParams: List[Boolean], span: Span) extends ClassDeclaration {
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, Value)] = l("ClassTail", x2, l("BindingIdentifier", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"class $x1 $x2"
  }
}

case class ClassDeclaration1(x1: ClassTail, parserParams: List[Boolean], span: Span) extends ClassDeclaration {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("ClassTail", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"class $x1"
  }
}
