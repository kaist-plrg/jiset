package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait BreakStatement extends AST { val kind: String = "BreakStatement" }

object AbsBreakStatement extends BreakStatement with AbsAST

object BreakStatement {
  def apply(data: Json): BreakStatement = AST(data) match {
    case Some(compressed) => BreakStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): BreakStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        BreakStatement0(params, span)
      case 1 =>
        val x0 = subs(0).map(LabelIdentifier(_)).get
        BreakStatement1(x0, params, span)
    }
  }
}

case class BreakStatement0(parserParams: List[Boolean], span: Span) extends BreakStatement {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"break ;"
  }
}

case class BreakStatement1(x2: LabelIdentifier, parserParams: List[Boolean], span: Span) extends BreakStatement {
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, 0)
  def fullList: List[(String, PureValue)] = l("LabelIdentifier", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"break $x2 ;"
  }
}
