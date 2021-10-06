package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait DefaultClause extends AST { val kind: String = "DefaultClause" }

object AbsDefaultClause extends DefaultClause with AbsAST

object DefaultClause {
  def apply(data: Json): DefaultClause = AST(data) match {
    case Some(compressed) => DefaultClause(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): DefaultClause = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(StatementList(_))
        DefaultClause0(x0, params, span)
    }
  }
}

case class DefaultClause0(x2: Option[StatementList], parserParams: List[Boolean], span: Span) extends DefaultClause {
  x2.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x2, 0)
  def fullList: List[(String, PureValue)] = l("Option[StatementList]", x2, Nil).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"default : ${x2.getOrElse("")}"
  }
}
