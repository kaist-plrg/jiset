package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait Identifier extends AST { val kind: String = "Identifier" }

case class Identifier0(x0: Lexical, parserParams: List[Boolean], span: Span) extends Identifier {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
