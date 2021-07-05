package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait Elision extends AST { val kind: String = "Elision" }

case class Elision0(parserParams: List[Boolean], span: Span) extends Elision {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s","
  }
}

case class Elision1(x0: Elision, parserParams: List[Boolean], span: Span) extends Elision {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("Elision", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ,"
  }
}
