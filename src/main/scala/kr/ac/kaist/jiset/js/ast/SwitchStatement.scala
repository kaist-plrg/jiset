package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait SwitchStatement extends AST { val kind: String = "SwitchStatement" }

object SwitchStatement {
  def apply(data: Json): SwitchStatement = AST(data) match {
    case Some(compressed) => SwitchStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): SwitchStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Expression(_)).get
        val x1 = subs(1).map(CaseBlock(_)).get
        SwitchStatement0(x0, x1, params, span)
    }
  }
}

case class SwitchStatement0(x2: Expression, x4: CaseBlock, parserParams: List[Boolean], span: Span) extends SwitchStatement {
  x2.parent = Some(this)
  x4.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x4, d(x2, 0))
  def fullList: List[(String, Value)] = l("CaseBlock", x4, l("Expression", x2, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"switch ( $x2 ) $x4"
  }
}
