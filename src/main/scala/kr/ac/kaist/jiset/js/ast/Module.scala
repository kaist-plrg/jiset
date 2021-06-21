package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait Module extends AST { val kind: String = "Module" }

case class Module0(x0: Option[ModuleBody], parserParams: List[Boolean], span: Span) extends Module {
  x0.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("Option[ModuleBody]", x0, Nil).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"${x0.getOrElse("")}"
  }
}
