package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ImportDeclaration extends AST { val kind: String = "ImportDeclaration" }

object ImportDeclaration {
  def apply(data: Json): ImportDeclaration = AST(data) match {
    case Some(compressed) => ImportDeclaration(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ImportDeclaration = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ImportClause(_)).get
        val x1 = subs(1).map(FromClause(_)).get
        ImportDeclaration0(x0, x1, params, span)
      case 1 =>
        val x0 = subs(0).map(ModuleSpecifier(_)).get
        ImportDeclaration1(x0, params, span)
    }
  }
}

case class ImportDeclaration0(x1: ImportClause, x2: FromClause, parserParams: List[Boolean], span: Span) extends ImportDeclaration {
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, PureValue)] = l("FromClause", x2, l("ImportClause", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"import $x1 $x2 ;"
  }
}

case class ImportDeclaration1(x1: ModuleSpecifier, parserParams: List[Boolean], span: Span) extends ImportDeclaration {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("ModuleSpecifier", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"import $x1 ;"
  }
}
