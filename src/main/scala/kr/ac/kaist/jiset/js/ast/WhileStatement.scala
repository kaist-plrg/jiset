package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait WhileStatement extends AST { val kind: String = "WhileStatement" }

object WhileStatement {
  def apply(data: Json): WhileStatement = AST(data) match {
    case Some(compressed) => WhileStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): WhileStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Expression(_)).get
        val x1 = subs(1).map(Statement(_)).get
        WhileStatement0(x0, x1, params, span)
    }
  }
}

case class WhileStatement0(x2: Expression, x4: Statement, parserParams: List[Boolean], span: Span) extends WhileStatement {
  x2.parent = Some(this)
  x4.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x4, d(x2, 0))
  def fullList: List[(String, PureValue)] = l("Statement", x4, l("Expression", x2, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"while ( $x2 ) $x4"
  }
}
