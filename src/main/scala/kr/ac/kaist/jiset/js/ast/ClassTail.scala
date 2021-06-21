package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ClassTail extends AST { val kind: String = "ClassTail" }

case class ClassTail0(x0: Option[ClassHeritage], x2: Option[ClassBody], parserParams: List[Boolean], span: Span) extends ClassTail {
  x0.foreach((m) => m.parent = Some(this))
  x2.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("Option[ClassBody]", x2, l("Option[ClassHeritage]", x0, Nil)).reverse
  def maxK: Int = 3
  override def toString: String = {
    s"${x0.getOrElse("")} { ${x2.getOrElse("")} }"
  }
}
