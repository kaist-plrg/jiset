package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ImportedBinding extends AST { val kind: String = "ImportedBinding" }

case class ImportedBinding0(x0: BindingIdentifier, parserParams: List[Boolean], span: Span) extends ImportedBinding {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("BindingIdentifier", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
