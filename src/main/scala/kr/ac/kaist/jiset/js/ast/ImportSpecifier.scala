package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ImportSpecifier extends AST { val kind: String = "ImportSpecifier" }

object ImportSpecifier {
  def apply(data: Json): ImportSpecifier = AST(data) match {
    case Some(compressed) => ImportSpecifier(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ImportSpecifier = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ImportedBinding(_)).get
        ImportSpecifier0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(Lexical(_)).get
        val x1 = subs(1).map(ImportedBinding(_)).get
        ImportSpecifier1(x0, x1, params, span)
    }
  }
}

case class ImportSpecifier0(x0: ImportedBinding, parserParams: List[Boolean], span: Span) extends ImportSpecifier {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ImportedBinding", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ImportSpecifier1(x0: Lexical, x2: ImportedBinding, parserParams: List[Boolean], span: Span) extends ImportSpecifier {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("ImportedBinding", x2, l("Lexical", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 as $x2"
  }
}
