package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait SuperProperty extends AST { val kind: String = "SuperProperty" }

case class SuperProperty0(x2: Expression, parserParams: List[Boolean], span: Span) extends SuperProperty {
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("Expression", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"super [ $x2 ]"
  }
}

case class SuperProperty1(x2: Lexical, parserParams: List[Boolean], span: Span) extends SuperProperty {
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("Lexical", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"super . $x2"
  }
}
