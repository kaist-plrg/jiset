package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ClassExpression extends AST { val kind: String = "ClassExpression" }

case class ClassExpression0(x1: Option[BindingIdentifier], x2: ClassTail, parserParams: List[Boolean], span: Span) extends ClassExpression {
  x1.foreach((m) => m.parent = Some(this))
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, Value)] = l("ClassTail", x2, l("Option[BindingIdentifier]", x1, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"class ${x1.getOrElse("")} $x2"
  }
}
