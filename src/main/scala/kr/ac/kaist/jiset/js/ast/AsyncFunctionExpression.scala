package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait AsyncFunctionExpression extends AST { val kind: String = "AsyncFunctionExpression" }

case class AsyncFunctionExpression0(x3: Option[BindingIdentifier], x5: FormalParameters, x8: AsyncFunctionBody, parserParams: List[Boolean], span: Span) extends AsyncFunctionExpression {
  x3.foreach((m) => m.parent = Some(this))
  x5.parent = Some(this)
  x8.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x8, d(x5, d(x3, 0)))
  def fullList: List[(String, Value)] = l("AsyncFunctionBody", x8, l("FormalParameters", x5, l("Option[BindingIdentifier]", x3, Nil))).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"async function ${x3.getOrElse("")} ( $x5 ) { $x8 }"
  }
}
