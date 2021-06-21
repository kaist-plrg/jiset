package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait NewTarget extends AST { val kind: String = "NewTarget" }

case class NewTarget0(parserParams: List[Boolean], span: Span) extends NewTarget {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"new . target"
  }
}
