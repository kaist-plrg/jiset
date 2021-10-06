package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait UniqueFormalParameters extends AST { val kind: String = "UniqueFormalParameters" }

object AbsUniqueFormalParameters extends UniqueFormalParameters with AbsAST

object UniqueFormalParameters {
  def apply(data: Json): UniqueFormalParameters = AST(data) match {
    case Some(compressed) => UniqueFormalParameters(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): UniqueFormalParameters = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(FormalParameters(_)).get
        UniqueFormalParameters0(x0, params, span)
    }
  }
}

case class UniqueFormalParameters0(x0: FormalParameters, parserParams: List[Boolean], span: Span) extends UniqueFormalParameters {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("FormalParameters", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
