package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait NamedImports extends AST { val kind: String = "NamedImports" }

object NamedImports {
  def apply(data: Json): NamedImports = AST(data) match {
    case Some(compressed) => NamedImports(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): NamedImports = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        NamedImports0(params, span)
      case 1 =>
        val x0 = subs(0).map(ImportsList(_)).get
        NamedImports1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(ImportsList(_)).get
        NamedImports2(x0, params, span)
    }
  }
}

case class NamedImports0(parserParams: List[Boolean], span: Span) extends NamedImports {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ }"
  }
}

case class NamedImports1(x1: ImportsList, parserParams: List[Boolean], span: Span) extends NamedImports {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("ImportsList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}

case class NamedImports2(x1: ImportsList, parserParams: List[Boolean], span: Span) extends NamedImports {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("ImportsList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 , }"
  }
}
