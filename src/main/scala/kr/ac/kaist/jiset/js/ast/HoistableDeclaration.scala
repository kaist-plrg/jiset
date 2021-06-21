package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait HoistableDeclaration extends AST { val kind: String = "HoistableDeclaration" }

case class HoistableDeclaration0(x0: FunctionDeclaration, parserParams: List[Boolean], span: Span) extends HoistableDeclaration {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("FunctionDeclaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class HoistableDeclaration1(x0: GeneratorDeclaration, parserParams: List[Boolean], span: Span) extends HoistableDeclaration {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("GeneratorDeclaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class HoistableDeclaration2(x0: AsyncFunctionDeclaration, parserParams: List[Boolean], span: Span) extends HoistableDeclaration {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("AsyncFunctionDeclaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class HoistableDeclaration3(x0: AsyncGeneratorDeclaration, parserParams: List[Boolean], span: Span) extends HoistableDeclaration {
  x0.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("AsyncGeneratorDeclaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
