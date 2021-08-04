package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait CaseBlock extends AST { val kind: String = "CaseBlock" }

object CaseBlock {
  def apply(data: Json): CaseBlock = AST(data) match {
    case Some(compressed) => CaseBlock(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): CaseBlock = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(CaseClauses(_))
        CaseBlock0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(CaseClauses(_))
        val x1 = subs(1).map(DefaultClause(_)).get
        val x2 = subs(2).map(CaseClauses(_))
        CaseBlock1(x0, x1, x2, params, span)
    }
  }
}

case class CaseBlock0(x1: Option[CaseClauses], parserParams: List[Boolean], span: Span) extends CaseBlock {
  x1.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("Option[CaseClauses]", x1, Nil).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"{ ${x1.getOrElse("")} }"
  }
}

case class CaseBlock1(x1: Option[CaseClauses], x2: DefaultClause, x3: Option[CaseClauses], parserParams: List[Boolean], span: Span) extends CaseBlock {
  x1.foreach((m) => m.parent = Some(this))
  x2.parent = Some(this)
  x3.foreach((m) => m.parent = Some(this))
  def idx: Int = 1
  def k: Int = d(x3, d(x2, d(x1, 0)))
  def fullList: List[(String, PureValue)] = l("Option[CaseClauses]1", x3, l("DefaultClause", x2, l("Option[CaseClauses]0", x1, Nil))).reverse
  def maxK: Int = 3
  override def toString: String = {
    s"{ ${x1.getOrElse("")} $x2 ${x3.getOrElse("")} }"
  }
}
