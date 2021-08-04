package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait BreakableStatement extends AST { val kind: String = "BreakableStatement" }

object BreakableStatement {
  def apply(data: Json): BreakableStatement = AST(data) match {
    case Some(compressed) => BreakableStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): BreakableStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(IterationStatement(_)).get
        BreakableStatement0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(SwitchStatement(_)).get
        BreakableStatement1(x0, params, span)
    }
  }
}

case class BreakableStatement0(x0: IterationStatement, parserParams: List[Boolean], span: Span) extends BreakableStatement {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("IterationStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BreakableStatement1(x0: SwitchStatement, parserParams: List[Boolean], span: Span) extends BreakableStatement {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("SwitchStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
