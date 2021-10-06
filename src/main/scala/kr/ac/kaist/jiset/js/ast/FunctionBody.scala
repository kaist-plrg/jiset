package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait FunctionBody extends AST { val kind: String = "FunctionBody" }

object AbsFunctionBody extends FunctionBody with AbsAST

object FunctionBody {
  def apply(data: Json): FunctionBody = AST(data) match {
    case Some(compressed) => FunctionBody(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): FunctionBody = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(FunctionStatementList(_)).get
        FunctionBody0(x0, params, span)
    }
  }
}

case class FunctionBody0(x0: FunctionStatementList, parserParams: List[Boolean], span: Span) extends FunctionBody {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("FunctionStatementList", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
