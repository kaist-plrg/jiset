package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait DefaultClause extends AST { val kind: String = "DefaultClause" }

case class DefaultClause0(x2: Option[StatementList], parserParams: List[Boolean], span: Span) extends DefaultClause {
  x2.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("Option[StatementList]", x2, Nil).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"default : ${x2.getOrElse("")}"
  }
}
