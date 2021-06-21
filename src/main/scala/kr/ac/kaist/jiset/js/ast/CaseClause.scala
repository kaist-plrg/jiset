package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait CaseClause extends AST { val kind: String = "CaseClause" }

case class CaseClause0(x1: Expression, x3: Option[StatementList], parserParams: List[Boolean], span: Span) extends CaseClause {
  x1.parent = Some(this)
  x3.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x3, d(x1, 0))
  def fullList: List[(String, Value)] = l("Option[StatementList]", x3, l("Expression", x1, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"case $x1 : ${x3.getOrElse("")}"
  }
}
