package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait CaseClause extends AST { val kind: String = "CaseClause" }

object AbsCaseClause extends CaseClause with AbsAST

object CaseClause {
  def apply(data: Json): CaseClause = AST(data) match {
    case Some(compressed) => CaseClause(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): CaseClause = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Expression(_)).get
        val x1 = subs(1).map(StatementList(_))
        CaseClause0(x0, x1, params, span)
    }
  }
}

case class CaseClause0(x1: Expression, x3: Option[StatementList], parserParams: List[Boolean], span: Span) extends CaseClause {
  x1.parent = Some(this)
  x3.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x3, d(x1, 0))
  def fullList: List[(String, PureValue)] = l("Option[StatementList]", x3, l("Expression", x1, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"case $x1 : ${x3.getOrElse("")}"
  }
}
