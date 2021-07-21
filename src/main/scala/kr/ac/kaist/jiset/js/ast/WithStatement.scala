package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait WithStatement extends AST { val kind: String = "WithStatement" }

object WithStatement {
  def apply(data: Json): WithStatement = AST(data) match {
    case Some(compressed) => WithStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): WithStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Expression(_)).get
        val x1 = subs(1).map(Statement(_)).get
        WithStatement0(x0, x1, params, span)
    }
  }
}

case class WithStatement0(x2: Expression, x4: Statement, parserParams: List[Boolean], span: Span) extends WithStatement {
  x2.parent = Some(this)
  x4.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x4, d(x2, 0))
  def fullList: List[(String, Value)] = l("Statement", x4, l("Expression", x2, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"with ( $x2 ) $x4"
  }
}
