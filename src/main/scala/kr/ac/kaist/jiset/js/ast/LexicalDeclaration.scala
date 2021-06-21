package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait LexicalDeclaration extends AST { val kind: String = "LexicalDeclaration" }

case class LexicalDeclaration0(x0: LetOrConst, x1: BindingList, parserParams: List[Boolean], span: Span) extends LexicalDeclaration {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("BindingList", x1, l("LetOrConst", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1 ;"
  }
}
