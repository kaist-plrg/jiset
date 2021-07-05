package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait NameSpaceImport extends AST { val kind: String = "NameSpaceImport" }

case class NameSpaceImport0(x2: ImportedBinding, parserParams: List[Boolean], span: Span) extends NameSpaceImport {
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("ImportedBinding", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"* as $x2"
  }
}
