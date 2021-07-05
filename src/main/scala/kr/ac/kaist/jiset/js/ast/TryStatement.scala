package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait TryStatement extends AST { val kind: String = "TryStatement" }

case class TryStatement0(x1: Block, x2: Catch, parserParams: List[Boolean], span: Span) extends TryStatement {
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, Value)] = l("Catch", x2, l("Block", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"try $x1 $x2"
  }
}

case class TryStatement1(x1: Block, x2: Finally, parserParams: List[Boolean], span: Span) extends TryStatement {
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, Value)] = l("Finally", x2, l("Block", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"try $x1 $x2"
  }
}

case class TryStatement2(x1: Block, x2: Catch, x3: Finally, parserParams: List[Boolean], span: Span) extends TryStatement {
  x1.parent = Some(this)
  x2.parent = Some(this)
  x3.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x3, d(x2, d(x1, 0)))
  def fullList: List[(String, Value)] = l("Finally", x3, l("Catch", x2, l("Block", x1, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"try $x1 $x2 $x3"
  }
}
