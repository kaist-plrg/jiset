package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ExportsList extends AST { val kind: String = "ExportsList" }

case class ExportsList0(x0: ExportSpecifier, parserParams: List[Boolean], span: Span) extends ExportsList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ExportSpecifier", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ExportsList1(x0: ExportsList, x2: ExportSpecifier, parserParams: List[Boolean], span: Span) extends ExportsList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("ExportSpecifier", x2, l("ExportsList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
