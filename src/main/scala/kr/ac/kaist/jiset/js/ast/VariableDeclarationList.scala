package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait VariableDeclarationList extends AST { val kind: String = "VariableDeclarationList" }

object VariableDeclarationList {
  def apply(data: Json): VariableDeclarationList = AST(data) match {
    case Some(compressed) => VariableDeclarationList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): VariableDeclarationList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(VariableDeclaration(_)).get
        VariableDeclarationList0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(VariableDeclarationList(_)).get
        val x1 = subs(1).map(VariableDeclaration(_)).get
        VariableDeclarationList1(x0, x1, params, span)
    }
  }
}

case class VariableDeclarationList0(x0: VariableDeclaration, parserParams: List[Boolean], span: Span) extends VariableDeclarationList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("VariableDeclaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class VariableDeclarationList1(x0: VariableDeclarationList, x2: VariableDeclaration, parserParams: List[Boolean], span: Span) extends VariableDeclarationList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("VariableDeclaration", x2, l("VariableDeclarationList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
