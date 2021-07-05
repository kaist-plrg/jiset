package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait MultiplicativeOperator extends AST { val kind: String = "MultiplicativeOperator" }

case class MultiplicativeOperator0(parserParams: List[Boolean], span: Span) extends MultiplicativeOperator {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"*"
  }
}

case class MultiplicativeOperator1(parserParams: List[Boolean], span: Span) extends MultiplicativeOperator {
  def idx: Int = 1
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"/"
  }
}

case class MultiplicativeOperator2(parserParams: List[Boolean], span: Span) extends MultiplicativeOperator {
  def idx: Int = 2
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"%"
  }
}
