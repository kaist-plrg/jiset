package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ClassElement extends AST { val kind: String = "ClassElement" }

object AbsClassElement extends ClassElement with AbsAST

object ClassElement {
  def apply(data: Json): ClassElement = AST(data) match {
    case Some(compressed) => ClassElement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ClassElement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(MethodDefinition(_)).get
        ClassElement0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(MethodDefinition(_)).get
        ClassElement1(x0, params, span)
      case 2 =>
        ClassElement2(params, span)
    }
  }
}

case class ClassElement0(x0: MethodDefinition, parserParams: List[Boolean], span: Span) extends ClassElement {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("MethodDefinition", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ClassElement1(x1: MethodDefinition, parserParams: List[Boolean], span: Span) extends ClassElement {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("MethodDefinition", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"static $x1"
  }
}

case class ClassElement2(parserParams: List[Boolean], span: Span) extends ClassElement {
  def idx: Int = 2
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s";"
  }
}
