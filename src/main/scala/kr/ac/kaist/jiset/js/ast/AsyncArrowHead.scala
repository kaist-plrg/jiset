package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AsyncArrowHead extends AST { val kind: String = "AsyncArrowHead" }

object AsyncArrowHead {
  def apply(data: Json): AsyncArrowHead = AST(data) match {
    case Some(compressed) => AsyncArrowHead(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AsyncArrowHead = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ArrowFormalParameters(_)).get
        AsyncArrowHead0(x0, params, span)
    }
  }
}

case class AsyncArrowHead0(x2: ArrowFormalParameters, parserParams: List[Boolean], span: Span) extends AsyncArrowHead {
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, 0)
  def fullList: List[(String, PureValue)] = l("ArrowFormalParameters", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"async $x2"
  }
}
