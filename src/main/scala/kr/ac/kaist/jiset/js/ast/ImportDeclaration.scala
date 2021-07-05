package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ImportDeclaration extends AST { val kind: String = "ImportDeclaration" }

case class ImportDeclaration0(x1: ImportClause, x2: FromClause, parserParams: List[Boolean], span: Span) extends ImportDeclaration {
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, Value)] = l("FromClause", x2, l("ImportClause", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"import $x1 $x2 ;"
  }
}

case class ImportDeclaration1(x1: ModuleSpecifier, parserParams: List[Boolean], span: Span) extends ImportDeclaration {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("ModuleSpecifier", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"import $x1 ;"
  }
}
