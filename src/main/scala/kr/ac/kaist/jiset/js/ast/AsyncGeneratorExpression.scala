package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait AsyncGeneratorExpression extends AST { val kind: String = "AsyncGeneratorExpression" }

case class AsyncGeneratorExpression0(x4: Option[BindingIdentifier], x6: FormalParameters, x9: AsyncGeneratorBody, parserParams: List[Boolean], span: Span) extends AsyncGeneratorExpression {
  x4.foreach((m) => m.parent = Some(this))
  x6.parent = Some(this)
  x9.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x9, d(x6, d(x4, 0)))
  def fullList: List[(String, Value)] = l("AsyncGeneratorBody", x9, l("FormalParameters", x6, l("Option[BindingIdentifier]", x4, Nil))).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"async function * ${x4.getOrElse("")} ( $x6 ) { $x9 }"
  }
}
