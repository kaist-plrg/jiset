package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.error.InvalidAST
import kr.ac.kaist.jiset.spec.grammar._
import kr.ac.kaist.jiset.util.{ Span, Pos }
import spray.json._

case class Lexical(kind: String, str: String) extends AST {
  def idx: Int = 0
  def k: Int = 0
  def parserParams: List[Boolean] = Nil
  def span: Span = Span()
  def fullList: List[(String, Value)] = Nil
  def maxK: Int = 0

  // name
  override def name: String = kind

  // to JSON format
  override def toJson: JsValue = JsString(str)

  // conversion to string
  override def toString: String = str
}
