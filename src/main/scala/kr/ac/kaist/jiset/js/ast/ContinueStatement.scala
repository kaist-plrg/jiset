package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ContinueStatement extends AST { val kind: String = "ContinueStatement" }

case class ContinueStatement0(parserParams: List[Boolean], span: Span) extends ContinueStatement {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"continue ;"
  }
}

case class ContinueStatement1(x2: LabelIdentifier, parserParams: List[Boolean], span: Span) extends ContinueStatement {
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("LabelIdentifier", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"continue $x2 ;"
  }
}
