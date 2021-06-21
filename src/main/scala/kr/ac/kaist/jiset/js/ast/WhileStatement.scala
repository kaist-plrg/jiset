package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait WhileStatement extends AST { val kind: String = "WhileStatement" }

case class WhileStatement0(x2: Expression, x4: Statement, parserParams: List[Boolean], span: Span) extends WhileStatement {
  x2.parent = Some(this)
  x4.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x4, d(x2, 0))
  def fullList: List[(String, Value)] = l("Statement", x4, l("Expression", x2, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"while ( $x2 ) $x4"
  }
}
