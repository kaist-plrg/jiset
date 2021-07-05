package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait FunctionBody extends AST { val kind: String = "FunctionBody" }

case class FunctionBody0(x0: FunctionStatementList, parserParams: List[Boolean], span: Span) extends FunctionBody {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("FunctionStatementList", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
