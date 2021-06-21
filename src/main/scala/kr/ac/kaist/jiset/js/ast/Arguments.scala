package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait Arguments extends AST { val kind: String = "Arguments" }

case class Arguments0(parserParams: List[Boolean], span: Span) extends Arguments {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( )"
  }
}

case class Arguments1(x1: ArgumentList, parserParams: List[Boolean], span: Span) extends Arguments {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("ArgumentList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( $x1 )"
  }
}

case class Arguments2(x1: ArgumentList, parserParams: List[Boolean], span: Span) extends Arguments {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("ArgumentList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( $x1 , )"
  }
}
