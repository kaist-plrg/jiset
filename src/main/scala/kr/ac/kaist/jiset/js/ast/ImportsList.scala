package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ImportsList extends AST { val kind: String = "ImportsList" }

case class ImportsList0(x0: ImportSpecifier, parserParams: List[Boolean], span: Span) extends ImportsList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ImportSpecifier", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ImportsList1(x0: ImportsList, x2: ImportSpecifier, parserParams: List[Boolean], span: Span) extends ImportsList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("ImportSpecifier", x2, l("ImportsList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
