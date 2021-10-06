package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

// Abstract AST
trait AbsAST extends AST {
  def idx: Int = -1
  def k: Int = -1
  def fullList: List[(String, PureValue)] = Nil
  def maxK: Int = -1
  def parserParams: List[Boolean] = Nil
  def span: Span = Span()
  override def toString = s"#$kind"
}
