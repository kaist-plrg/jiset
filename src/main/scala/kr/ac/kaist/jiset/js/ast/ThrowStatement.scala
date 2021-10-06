package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ThrowStatement extends AST { val kind: String = "ThrowStatement" }

object AbsThrowStatement extends ThrowStatement with AbsAST

object ThrowStatement {
  def apply(data: Json): ThrowStatement = AST(data) match {
    case Some(compressed) => ThrowStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ThrowStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Expression(_)).get
        ThrowStatement0(x0, params, span)
    }
  }
}

case class ThrowStatement0(x2: Expression, parserParams: List[Boolean], span: Span) extends ThrowStatement {
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, 0)
  def fullList: List[(String, PureValue)] = l("Expression", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"throw $x2 ;"
  }
}
