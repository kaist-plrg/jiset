package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait AsyncGeneratorMethod extends AST { val kind: String = "AsyncGeneratorMethod" }

case class AsyncGeneratorMethod0(x3: PropertyName, x5: UniqueFormalParameters, x8: AsyncGeneratorBody, parserParams: List[Boolean], span: Span) extends AsyncGeneratorMethod {
  x3.parent = Some(this)
  x5.parent = Some(this)
  x8.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x8, d(x5, d(x3, 0)))
  def fullList: List[(String, Value)] = l("AsyncGeneratorBody", x8, l("UniqueFormalParameters", x5, l("PropertyName", x3, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"async * $x3 ( $x5 ) { $x8 }"
  }
}
