package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait MetaProperty extends AST { val kind: String = "MetaProperty" }

case class MetaProperty0(x0: NewTarget, parserParams: List[Boolean], span: Span) extends MetaProperty {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("NewTarget", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class MetaProperty1(x0: ImportMeta, parserParams: List[Boolean], span: Span) extends MetaProperty {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ImportMeta", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
