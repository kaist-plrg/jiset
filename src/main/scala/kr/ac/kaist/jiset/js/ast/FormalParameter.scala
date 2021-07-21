package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait FormalParameter extends AST { val kind: String = "FormalParameter" }

object FormalParameter {
  def apply(data: Json): FormalParameter = AST(data) match {
    case Some(compressed) => FormalParameter(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): FormalParameter = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingElement(_)).get
        FormalParameter0(x0, params, span)
    }
  }
}

case class FormalParameter0(x0: BindingElement, parserParams: List[Boolean], span: Span) extends FormalParameter {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("BindingElement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
