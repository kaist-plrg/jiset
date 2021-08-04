package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ClassDeclaration extends AST { val kind: String = "ClassDeclaration" }

object ClassDeclaration {
  def apply(data: Json): ClassDeclaration = AST(data) match {
    case Some(compressed) => ClassDeclaration(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ClassDeclaration = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingIdentifier(_)).get
        val x1 = subs(1).map(ClassTail(_)).get
        ClassDeclaration0(x0, x1, params, span)
      case 1 =>
        val x0 = subs(0).map(ClassTail(_)).get
        ClassDeclaration1(x0, params, span)
    }
  }
}

case class ClassDeclaration0(x1: BindingIdentifier, x2: ClassTail, parserParams: List[Boolean], span: Span) extends ClassDeclaration {
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, PureValue)] = l("ClassTail", x2, l("BindingIdentifier", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"class $x1 $x2"
  }
}

case class ClassDeclaration1(x1: ClassTail, parserParams: List[Boolean], span: Span) extends ClassDeclaration {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("ClassTail", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"class $x1"
  }
}
