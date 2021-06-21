package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait Block extends AST { val kind: String = "Block" }

case class Block0(x1: Option[StatementList], parserParams: List[Boolean], span: Span) extends Block {
  x1.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Option[StatementList]", x1, Nil).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"{ ${x1.getOrElse("")} }"
  }
}
