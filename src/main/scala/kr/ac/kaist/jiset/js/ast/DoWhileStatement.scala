package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait DoWhileStatement extends AST { val kind: String = "DoWhileStatement" }

case class DoWhileStatement0(x1: Statement, x4: Expression, parserParams: List[Boolean], span: Span) extends DoWhileStatement {
  x1.parent = Some(this)
  x4.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x4, d(x1, 0))
  def fullList: List[(String, Value)] = l("Expression", x4, l("Statement", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"do $x1 while ( $x4 ) ;"
  }
}
