package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait VariableStatement extends AST { val kind: String = "VariableStatement" }

case class VariableStatement0(x1: VariableDeclarationList, parserParams: List[Boolean], span: Span) extends VariableStatement {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("VariableDeclarationList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"var $x1 ;"
  }
}
