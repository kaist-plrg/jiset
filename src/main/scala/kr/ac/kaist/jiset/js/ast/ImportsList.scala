package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ImportsList extends AST { val kind: String = "ImportsList" }

object ImportsList {
  def apply(data: Json): ImportsList = AST(data) match {
    case Some(compressed) => ImportsList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ImportsList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ImportSpecifier(_)).get
        ImportsList0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(ImportsList(_)).get
        val x1 = subs(1).map(ImportSpecifier(_)).get
        ImportsList1(x0, x1, params, span)
    }
  }
}

case class ImportsList0(x0: ImportSpecifier, parserParams: List[Boolean], span: Span) extends ImportsList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ImportSpecifier", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ImportsList1(x0: ImportsList, x2: ImportSpecifier, parserParams: List[Boolean], span: Span) extends ImportsList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("ImportSpecifier", x2, l("ImportsList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
