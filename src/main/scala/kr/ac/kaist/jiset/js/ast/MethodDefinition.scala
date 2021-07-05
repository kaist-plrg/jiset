package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait MethodDefinition extends AST { val kind: String = "MethodDefinition" }

case class MethodDefinition0(x0: PropertyName, x2: UniqueFormalParameters, x5: FunctionBody, parserParams: List[Boolean], span: Span) extends MethodDefinition {
  x0.parent = Some(this)
  x2.parent = Some(this)
  x5.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x5, d(x2, d(x0, 0)))
  def fullList: List[(String, Value)] = l("FunctionBody", x5, l("UniqueFormalParameters", x2, l("PropertyName", x0, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ( $x2 ) { $x5 }"
  }
}

case class MethodDefinition1(x0: GeneratorMethod, parserParams: List[Boolean], span: Span) extends MethodDefinition {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("GeneratorMethod", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class MethodDefinition2(x0: AsyncMethod, parserParams: List[Boolean], span: Span) extends MethodDefinition {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("AsyncMethod", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class MethodDefinition3(x0: AsyncGeneratorMethod, parserParams: List[Boolean], span: Span) extends MethodDefinition {
  x0.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("AsyncGeneratorMethod", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class MethodDefinition4(x1: PropertyName, x5: FunctionBody, parserParams: List[Boolean], span: Span) extends MethodDefinition {
  x1.parent = Some(this)
  x5.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x5, d(x1, 0))
  def fullList: List[(String, Value)] = l("FunctionBody", x5, l("PropertyName", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"get $x1 ( ) { $x5 }"
  }
}

case class MethodDefinition5(x1: PropertyName, x3: PropertySetParameterList, x6: FunctionBody, parserParams: List[Boolean], span: Span) extends MethodDefinition {
  x1.parent = Some(this)
  x3.parent = Some(this)
  x6.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x6, d(x3, d(x1, 0)))
  def fullList: List[(String, Value)] = l("FunctionBody", x6, l("PropertySetParameterList", x3, l("PropertyName", x1, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"set $x1 ( $x3 ) { $x6 }"
  }
}
