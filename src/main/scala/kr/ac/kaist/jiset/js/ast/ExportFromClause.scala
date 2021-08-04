package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ExportFromClause extends AST { val kind: String = "ExportFromClause" }

object ExportFromClause {
  def apply(data: Json): ExportFromClause = AST(data) match {
    case Some(compressed) => ExportFromClause(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ExportFromClause = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        ExportFromClause0(params, span)
      case 1 =>
        val x0 = subs(0).map(Lexical(_)).get
        ExportFromClause1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(NamedExports(_)).get
        ExportFromClause2(x0, params, span)
    }
  }
}

case class ExportFromClause0(parserParams: List[Boolean], span: Span) extends ExportFromClause {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"*"
  }
}

case class ExportFromClause1(x2: Lexical, parserParams: List[Boolean], span: Span) extends ExportFromClause {
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, 0)
  def fullList: List[(String, PureValue)] = l("Lexical", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"* as $x2"
  }
}

case class ExportFromClause2(x0: NamedExports, parserParams: List[Boolean], span: Span) extends ExportFromClause {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("NamedExports", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
