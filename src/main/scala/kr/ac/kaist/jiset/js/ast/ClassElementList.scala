package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ClassElementList extends AST { val kind: String = "ClassElementList" }

object AbsClassElementList extends ClassElementList with AbsAST

object ClassElementList {
  def apply(data: Json): ClassElementList = AST(data) match {
    case Some(compressed) => ClassElementList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ClassElementList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ClassElement(_)).get
        ClassElementList0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(ClassElementList(_)).get
        val x1 = subs(1).map(ClassElement(_)).get
        ClassElementList1(x0, x1, params, span)
    }
  }
}

case class ClassElementList0(x0: ClassElement, parserParams: List[Boolean], span: Span) extends ClassElementList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ClassElement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ClassElementList1(x0: ClassElementList, x1: ClassElement, parserParams: List[Boolean], span: Span) extends ClassElementList {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("ClassElement", x1, l("ClassElementList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
