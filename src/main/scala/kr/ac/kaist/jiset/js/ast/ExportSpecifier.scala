package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ExportSpecifier extends AST { val kind: String = "ExportSpecifier" }

object AbsExportSpecifier extends ExportSpecifier with AbsAST

object ExportSpecifier {
  def apply(data: Json): ExportSpecifier = AST(data) match {
    case Some(compressed) => ExportSpecifier(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ExportSpecifier = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Lexical(_)).get
        ExportSpecifier0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(Lexical(_)).get
        val x1 = subs(1).map(Lexical(_)).get
        ExportSpecifier1(x0, x1, params, span)
    }
  }
}

case class ExportSpecifier0(x0: Lexical, parserParams: List[Boolean], span: Span) extends ExportSpecifier {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ExportSpecifier1(x0: Lexical, x2: Lexical, parserParams: List[Boolean], span: Span) extends ExportSpecifier {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("Lexical1", x2, l("Lexical0", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 as $x2"
  }
}
