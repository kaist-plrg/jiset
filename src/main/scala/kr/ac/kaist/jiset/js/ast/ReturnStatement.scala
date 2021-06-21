package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ReturnStatement extends AST { val kind: String = "ReturnStatement" }

case class ReturnStatement0(parserParams: List[Boolean], span: Span) extends ReturnStatement {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"return ;"
  }
}

case class ReturnStatement1(x2: Expression, parserParams: List[Boolean], span: Span) extends ReturnStatement {
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("Expression", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"return $x2 ;"
  }
}
