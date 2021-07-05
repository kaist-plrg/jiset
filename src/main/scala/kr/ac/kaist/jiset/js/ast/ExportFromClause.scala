package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ExportFromClause extends AST { val kind: String = "ExportFromClause" }

case class ExportFromClause0(parserParams: List[Boolean], span: Span) extends ExportFromClause {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"*"
  }
}

case class ExportFromClause1(x2: Lexical, parserParams: List[Boolean], span: Span) extends ExportFromClause {
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("Lexical", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"* as $x2"
  }
}

case class ExportFromClause2(x0: NamedExports, parserParams: List[Boolean], span: Span) extends ExportFromClause {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("NamedExports", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
