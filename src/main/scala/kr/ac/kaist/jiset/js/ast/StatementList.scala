package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait StatementList extends AST { val kind: String = "StatementList" }

object StatementList {
  def apply(data: Json): StatementList = AST(data) match {
    case Some(compressed) => StatementList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): StatementList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(StatementListItem(_)).get
        StatementList0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(StatementList(_)).get
        val x1 = subs(1).map(StatementListItem(_)).get
        StatementList1(x0, x1, params, span)
    }
  }
}

case class StatementList0(x0: StatementListItem, parserParams: List[Boolean], span: Span) extends StatementList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("StatementListItem", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class StatementList1(x0: StatementList, x1: StatementListItem, parserParams: List[Boolean], span: Span) extends StatementList {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("StatementListItem", x1, l("StatementList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
