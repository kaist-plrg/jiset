package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait Finally extends AST { val kind: String = "Finally" }

case class Finally0(x1: Block, parserParams: List[Boolean], span: Span) extends Finally {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Block", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"finally $x1"
  }
}
