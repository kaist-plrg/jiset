package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ArrowFormalParameters extends AST { val kind: String = "ArrowFormalParameters" }

object ArrowFormalParameters {
  def apply(data: Json): ArrowFormalParameters = AST(data) match {
    case Some(compressed) => ArrowFormalParameters(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ArrowFormalParameters = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(UniqueFormalParameters(_)).get
        ArrowFormalParameters0(x0, params, span)
    }
  }
}

case class ArrowFormalParameters0(x1: UniqueFormalParameters, parserParams: List[Boolean], span: Span) extends ArrowFormalParameters {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("UniqueFormalParameters", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( $x1 )"
  }
}
