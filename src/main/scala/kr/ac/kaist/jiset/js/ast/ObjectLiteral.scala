package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ObjectLiteral extends AST { val kind: String = "ObjectLiteral" }

case class ObjectLiteral0(parserParams: List[Boolean], span: Span) extends ObjectLiteral {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ }"
  }
}

case class ObjectLiteral1(x1: PropertyDefinitionList, parserParams: List[Boolean], span: Span) extends ObjectLiteral {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("PropertyDefinitionList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}

case class ObjectLiteral2(x1: PropertyDefinitionList, parserParams: List[Boolean], span: Span) extends ObjectLiteral {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("PropertyDefinitionList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 , }"
  }
}
