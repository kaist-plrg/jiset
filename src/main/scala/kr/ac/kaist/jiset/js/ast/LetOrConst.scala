package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait LetOrConst extends AST { val kind: String = "LetOrConst" }

case class LetOrConst0(parserParams: List[Boolean], span: Span) extends LetOrConst {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"let"
  }
}

case class LetOrConst1(parserParams: List[Boolean], span: Span) extends LetOrConst {
  def idx: Int = 1
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"const"
  }
}
