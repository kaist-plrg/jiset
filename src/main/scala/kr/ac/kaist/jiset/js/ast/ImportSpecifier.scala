package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ImportSpecifier extends AST { val kind: String = "ImportSpecifier" }

case class ImportSpecifier0(x0: ImportedBinding, parserParams: List[Boolean], span: Span) extends ImportSpecifier {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ImportedBinding", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ImportSpecifier1(x0: Lexical, x2: ImportedBinding, parserParams: List[Boolean], span: Span) extends ImportSpecifier {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("ImportedBinding", x2, l("Lexical", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 as $x2"
  }
}
