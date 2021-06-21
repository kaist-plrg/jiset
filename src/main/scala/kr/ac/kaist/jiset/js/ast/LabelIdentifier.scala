package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait LabelIdentifier extends AST { val kind: String = "LabelIdentifier" }

case class LabelIdentifier0(x0: Identifier, parserParams: List[Boolean], span: Span) extends LabelIdentifier {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("Identifier", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class LabelIdentifier1(parserParams: List[Boolean], span: Span) extends LabelIdentifier {
  def idx: Int = 1
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"yield"
  }
}

case class LabelIdentifier2(parserParams: List[Boolean], span: Span) extends LabelIdentifier {
  def idx: Int = 2
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"await"
  }
}
