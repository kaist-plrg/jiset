package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ExportSpecifier extends AST { val kind: String = "ExportSpecifier" }

case class ExportSpecifier0(x0: Lexical, parserParams: List[Boolean], span: Span) extends ExportSpecifier {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ExportSpecifier1(x0: Lexical, x2: Lexical, parserParams: List[Boolean], span: Span) extends ExportSpecifier {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("Lexical1", x2, l("Lexical0", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 as $x2"
  }
}
