package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait AsyncGeneratorDeclaration extends AST { val kind: String = "AsyncGeneratorDeclaration" }

case class AsyncGeneratorDeclaration0(x4: BindingIdentifier, x6: FormalParameters, x9: AsyncGeneratorBody, parserParams: List[Boolean], span: Span) extends AsyncGeneratorDeclaration {
  x4.parent = Some(this)
  x6.parent = Some(this)
  x9.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x9, d(x6, d(x4, 0)))
  def fullList: List[(String, Value)] = l("AsyncGeneratorBody", x9, l("FormalParameters", x6, l("BindingIdentifier", x4, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"async function * $x4 ( $x6 ) { $x9 }"
  }
}

case class AsyncGeneratorDeclaration1(x5: FormalParameters, x8: AsyncGeneratorBody, parserParams: List[Boolean], span: Span) extends AsyncGeneratorDeclaration {
  x5.parent = Some(this)
  x8.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x8, d(x5, 0))
  def fullList: List[(String, Value)] = l("AsyncGeneratorBody", x8, l("FormalParameters", x5, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"async function * ( $x5 ) { $x8 }"
  }
}
