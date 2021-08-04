package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait CaseClauses extends AST { val kind: String = "CaseClauses" }

object CaseClauses {
  def apply(data: Json): CaseClauses = AST(data) match {
    case Some(compressed) => CaseClauses(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): CaseClauses = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(CaseClause(_)).get
        CaseClauses0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(CaseClauses(_)).get
        val x1 = subs(1).map(CaseClause(_)).get
        CaseClauses1(x0, x1, params, span)
    }
  }
}

case class CaseClauses0(x0: CaseClause, parserParams: List[Boolean], span: Span) extends CaseClauses {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("CaseClause", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class CaseClauses1(x0: CaseClauses, x1: CaseClause, parserParams: List[Boolean], span: Span) extends CaseClauses {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("CaseClause", x1, l("CaseClauses", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
