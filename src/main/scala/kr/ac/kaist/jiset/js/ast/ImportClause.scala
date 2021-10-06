package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ImportClause extends AST { val kind: String = "ImportClause" }

object AbsImportClause extends ImportClause with AbsAST

object ImportClause {
  def apply(data: Json): ImportClause = AST(data) match {
    case Some(compressed) => ImportClause(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ImportClause = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ImportedDefaultBinding(_)).get
        ImportClause0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(NameSpaceImport(_)).get
        ImportClause1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(NamedImports(_)).get
        ImportClause2(x0, params, span)
      case 3 =>
        val x0 = subs(0).map(ImportedDefaultBinding(_)).get
        val x1 = subs(1).map(NameSpaceImport(_)).get
        ImportClause3(x0, x1, params, span)
      case 4 =>
        val x0 = subs(0).map(ImportedDefaultBinding(_)).get
        val x1 = subs(1).map(NamedImports(_)).get
        ImportClause4(x0, x1, params, span)
    }
  }
}

case class ImportClause0(x0: ImportedDefaultBinding, parserParams: List[Boolean], span: Span) extends ImportClause {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ImportedDefaultBinding", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ImportClause1(x0: NameSpaceImport, parserParams: List[Boolean], span: Span) extends ImportClause {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("NameSpaceImport", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ImportClause2(x0: NamedImports, parserParams: List[Boolean], span: Span) extends ImportClause {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("NamedImports", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ImportClause3(x0: ImportedDefaultBinding, x2: NameSpaceImport, parserParams: List[Boolean], span: Span) extends ImportClause {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("NameSpaceImport", x2, l("ImportedDefaultBinding", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}

case class ImportClause4(x0: ImportedDefaultBinding, x2: NamedImports, parserParams: List[Boolean], span: Span) extends ImportClause {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("NamedImports", x2, l("ImportedDefaultBinding", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
