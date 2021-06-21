package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait NamedExports extends AST { val kind: String = "NamedExports" }

case class NamedExports0(parserParams: List[Boolean], span: Span) extends NamedExports {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ }"
  }
}

case class NamedExports1(x1: ExportsList, parserParams: List[Boolean], span: Span) extends NamedExports {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("ExportsList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}

case class NamedExports2(x1: ExportsList, parserParams: List[Boolean], span: Span) extends NamedExports {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("ExportsList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 , }"
  }
}
