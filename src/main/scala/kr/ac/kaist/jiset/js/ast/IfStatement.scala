package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait IfStatement extends AST { val kind: String = "IfStatement" }

object IfStatement {
  def apply(data: Json): IfStatement = AST(data) match {
    case Some(compressed) => IfStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): IfStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Expression(_)).get
        val x1 = subs(1).map(Statement(_)).get
        val x2 = subs(2).map(Statement(_)).get
        IfStatement0(x0, x1, x2, params, span)
      case 1 =>
        val x0 = subs(0).map(Expression(_)).get
        val x1 = subs(1).map(Statement(_)).get
        IfStatement1(x0, x1, params, span)
    }
  }
}

case class IfStatement0(x2: Expression, x4: Statement, x6: Statement, parserParams: List[Boolean], span: Span) extends IfStatement {
  x2.parent = Some(this)
  x4.parent = Some(this)
  x6.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x6, d(x4, d(x2, 0)))
  def fullList: List[(String, Value)] = l("Statement1", x6, l("Statement0", x4, l("Expression", x2, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"if ( $x2 ) $x4 else $x6"
  }
}

case class IfStatement1(x2: Expression, x4: Statement, parserParams: List[Boolean], span: Span) extends IfStatement {
  x2.parent = Some(this)
  x4.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x4, d(x2, 0))
  def fullList: List[(String, Value)] = l("Statement", x4, l("Expression", x2, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"if ( $x2 ) $x4"
  }
}
