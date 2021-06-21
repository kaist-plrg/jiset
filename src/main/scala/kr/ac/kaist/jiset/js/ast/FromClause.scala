package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait FromClause extends AST { val kind: String = "FromClause" }

case class FromClause0(x1: ModuleSpecifier, parserParams: List[Boolean], span: Span) extends FromClause {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("ModuleSpecifier", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"from $x1"
  }
}
