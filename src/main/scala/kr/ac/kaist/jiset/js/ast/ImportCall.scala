package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ImportCall extends AST { val kind: String = "ImportCall" }

case class ImportCall0(x2: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ImportCall {
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("AssignmentExpression", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"import ( $x2 )"
  }
}
