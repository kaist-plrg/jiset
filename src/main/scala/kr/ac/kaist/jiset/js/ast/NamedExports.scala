package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait NamedExports extends AST { val kind: String = "NamedExports" }

object AbsNamedExports extends NamedExports with AbsAST

object NamedExports {
  def apply(data: Json): NamedExports = AST(data) match {
    case Some(compressed) => NamedExports(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): NamedExports = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        NamedExports0(params, span)
      case 1 =>
        val x0 = subs(0).map(ExportsList(_)).get
        NamedExports1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(ExportsList(_)).get
        NamedExports2(x0, params, span)
    }
  }
}

case class NamedExports0(parserParams: List[Boolean], span: Span) extends NamedExports {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ }"
  }
}

case class NamedExports1(x1: ExportsList, parserParams: List[Boolean], span: Span) extends NamedExports {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("ExportsList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 }"
  }
}

case class NamedExports2(x1: ExportsList, parserParams: List[Boolean], span: Span) extends NamedExports {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("ExportsList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"{ $x1 , }"
  }
}
