package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ClassHeritage extends AST { val kind: String = "ClassHeritage" }

case class ClassHeritage0(x1: LeftHandSideExpression, parserParams: List[Boolean], span: Span) extends ClassHeritage {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("LeftHandSideExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"extends $x1"
  }
}
