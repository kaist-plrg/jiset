package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait DoWhileStatement extends AST { val kind: String = "DoWhileStatement" }

object AbsDoWhileStatement extends DoWhileStatement with AbsAST

object DoWhileStatement {
  def apply(data: Json): DoWhileStatement = AST(data) match {
    case Some(compressed) => DoWhileStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): DoWhileStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Statement(_)).get
        val x1 = subs(1).map(Expression(_)).get
        DoWhileStatement0(x0, x1, params, span)
    }
  }
}

case class DoWhileStatement0(x1: Statement, x4: Expression, parserParams: List[Boolean], span: Span) extends DoWhileStatement {
  x1.parent = Some(this)
  x4.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x4, d(x1, 0))
  def fullList: List[(String, PureValue)] = l("Expression", x4, l("Statement", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"do $x1 while ( $x4 ) ;"
  }
}
