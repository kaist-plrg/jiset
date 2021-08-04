package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ModuleItem extends AST { val kind: String = "ModuleItem" }

object ModuleItem {
  def apply(data: Json): ModuleItem = AST(data) match {
    case Some(compressed) => ModuleItem(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ModuleItem = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ImportDeclaration(_)).get
        ModuleItem0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(ExportDeclaration(_)).get
        ModuleItem1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(StatementListItem(_)).get
        ModuleItem2(x0, params, span)
    }
  }
}

case class ModuleItem0(x0: ImportDeclaration, parserParams: List[Boolean], span: Span) extends ModuleItem {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ImportDeclaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ModuleItem1(x0: ExportDeclaration, parserParams: List[Boolean], span: Span) extends ModuleItem {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ExportDeclaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ModuleItem2(x0: StatementListItem, parserParams: List[Boolean], span: Span) extends ModuleItem {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("StatementListItem", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
