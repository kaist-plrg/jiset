package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait EmptyStatement extends AST { val kind: String = "EmptyStatement" }

case class EmptyStatement0(parserParams: List[Boolean], span: Span) extends EmptyStatement {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s";"
  }
}
