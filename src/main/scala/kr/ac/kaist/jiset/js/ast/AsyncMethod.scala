package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait AsyncMethod extends AST { val kind: String = "AsyncMethod" }

case class AsyncMethod0(x2: PropertyName, x4: UniqueFormalParameters, x7: AsyncFunctionBody, parserParams: List[Boolean], span: Span) extends AsyncMethod {
  x2.parent = Some(this)
  x4.parent = Some(this)
  x7.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x7, d(x4, d(x2, 0)))
  def fullList: List[(String, Value)] = l("AsyncFunctionBody", x7, l("UniqueFormalParameters", x4, l("PropertyName", x2, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"async $x2 ( $x4 ) { $x7 }"
  }
}
