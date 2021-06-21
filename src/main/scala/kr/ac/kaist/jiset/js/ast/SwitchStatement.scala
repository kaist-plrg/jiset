package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait SwitchStatement extends AST { val kind: String = "SwitchStatement" }

case class SwitchStatement0(x2: Expression, x4: CaseBlock, parserParams: List[Boolean], span: Span) extends SwitchStatement {
  x2.parent = Some(this)
  x4.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x4, d(x2, 0))
  def fullList: List[(String, Value)] = l("CaseBlock", x4, l("Expression", x2, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"switch ( $x2 ) $x4"
  }
}
