package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait FunctionDeclaration extends AST { val kind: String = "FunctionDeclaration" }

case class FunctionDeclaration0(x1: BindingIdentifier, x3: FormalParameters, x6: FunctionBody, parserParams: List[Boolean], span: Span) extends FunctionDeclaration {
  x1.parent = Some(this)
  x3.parent = Some(this)
  x6.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x6, d(x3, d(x1, 0)))
  def fullList: List[(String, Value)] = l("FunctionBody", x6, l("FormalParameters", x3, l("BindingIdentifier", x1, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"function $x1 ( $x3 ) { $x6 }"
  }
}

case class FunctionDeclaration1(x2: FormalParameters, x5: FunctionBody, parserParams: List[Boolean], span: Span) extends FunctionDeclaration {
  x2.parent = Some(this)
  x5.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x5, d(x2, 0))
  def fullList: List[(String, Value)] = l("FunctionBody", x5, l("FormalParameters", x2, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"function ( $x2 ) { $x5 }"
  }
}
