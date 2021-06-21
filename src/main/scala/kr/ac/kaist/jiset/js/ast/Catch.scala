package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait Catch extends AST { val kind: String = "Catch" }

case class Catch0(x2: CatchParameter, x4: Block, parserParams: List[Boolean], span: Span) extends Catch {
  x2.parent = Some(this)
  x4.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x4, d(x2, 0))
  def fullList: List[(String, Value)] = l("Block", x4, l("CatchParameter", x2, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"catch ( $x2 ) $x4"
  }
}

case class Catch1(x1: Block, parserParams: List[Boolean], span: Span) extends Catch {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Block", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"catch $x1"
  }
}
