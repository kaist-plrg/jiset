package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ExportsList extends AST { val kind: String = "ExportsList" }

object AbsExportsList extends ExportsList with AbsAST

object ExportsList {
  def apply(data: Json): ExportsList = AST(data) match {
    case Some(compressed) => ExportsList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ExportsList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ExportSpecifier(_)).get
        ExportsList0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(ExportsList(_)).get
        val x1 = subs(1).map(ExportSpecifier(_)).get
        ExportsList1(x0, x1, params, span)
    }
  }
}

case class ExportsList0(x0: ExportSpecifier, parserParams: List[Boolean], span: Span) extends ExportsList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ExportSpecifier", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ExportsList1(x0: ExportsList, x2: ExportSpecifier, parserParams: List[Boolean], span: Span) extends ExportsList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("ExportSpecifier", x2, l("ExportsList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
