package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait CaseClauses extends AST { val kind: String = "CaseClauses" }

case class CaseClauses0(x0: CaseClause, parserParams: List[Boolean], span: Span) extends CaseClauses {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("CaseClause", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class CaseClauses1(x0: CaseClauses, x1: CaseClause, parserParams: List[Boolean], span: Span) extends CaseClauses {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("CaseClause", x1, l("CaseClauses", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
