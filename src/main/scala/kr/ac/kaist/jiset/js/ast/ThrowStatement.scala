package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ThrowStatement extends AST { val kind: String = "ThrowStatement" }

case class ThrowStatement0(x2: Expression, parserParams: List[Boolean], span: Span) extends ThrowStatement {
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("Expression", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"throw $x2 ;"
  }
}
