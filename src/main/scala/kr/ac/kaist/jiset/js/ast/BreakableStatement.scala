package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait BreakableStatement extends AST { val kind: String = "BreakableStatement" }

case class BreakableStatement0(x0: IterationStatement, parserParams: List[Boolean], span: Span) extends BreakableStatement {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("IterationStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BreakableStatement1(x0: SwitchStatement, parserParams: List[Boolean], span: Span) extends BreakableStatement {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("SwitchStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
