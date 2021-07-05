package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait FormalParameters extends AST { val kind: String = "FormalParameters" }

case class FormalParameters0(parserParams: List[Boolean], span: Span) extends FormalParameters {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s""
  }
}

case class FormalParameters1(x0: FunctionRestParameter, parserParams: List[Boolean], span: Span) extends FormalParameters {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("FunctionRestParameter", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class FormalParameters2(x0: FormalParameterList, parserParams: List[Boolean], span: Span) extends FormalParameters {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("FormalParameterList", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class FormalParameters3(x0: FormalParameterList, parserParams: List[Boolean], span: Span) extends FormalParameters {
  x0.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("FormalParameterList", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ,"
  }
}

case class FormalParameters4(x0: FormalParameterList, x2: FunctionRestParameter, parserParams: List[Boolean], span: Span) extends FormalParameters {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("FunctionRestParameter", x2, l("FormalParameterList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
