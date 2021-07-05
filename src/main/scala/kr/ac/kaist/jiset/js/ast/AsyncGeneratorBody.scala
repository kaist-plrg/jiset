package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait AsyncGeneratorBody extends AST { val kind: String = "AsyncGeneratorBody" }

case class AsyncGeneratorBody0(x0: FunctionBody, parserParams: List[Boolean], span: Span) extends AsyncGeneratorBody {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("FunctionBody", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
