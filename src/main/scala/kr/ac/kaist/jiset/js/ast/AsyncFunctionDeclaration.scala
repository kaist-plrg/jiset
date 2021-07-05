package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait AsyncFunctionDeclaration extends AST { val kind: String = "AsyncFunctionDeclaration" }

case class AsyncFunctionDeclaration0(x3: BindingIdentifier, x5: FormalParameters, x8: AsyncFunctionBody, parserParams: List[Boolean], span: Span) extends AsyncFunctionDeclaration {
  x3.parent = Some(this)
  x5.parent = Some(this)
  x8.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x8, d(x5, d(x3, 0)))
  def fullList: List[(String, Value)] = l("AsyncFunctionBody", x8, l("FormalParameters", x5, l("BindingIdentifier", x3, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"async function $x3 ( $x5 ) { $x8 }"
  }
}

case class AsyncFunctionDeclaration1(x4: FormalParameters, x7: AsyncFunctionBody, parserParams: List[Boolean], span: Span) extends AsyncFunctionDeclaration {
  x4.parent = Some(this)
  x7.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x7, d(x4, 0))
  def fullList: List[(String, Value)] = l("AsyncFunctionBody", x7, l("FormalParameters", x4, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"async function ( $x4 ) { $x7 }"
  }
}
