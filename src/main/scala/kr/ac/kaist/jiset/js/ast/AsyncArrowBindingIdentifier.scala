package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AsyncArrowBindingIdentifier extends AST { val kind: String = "AsyncArrowBindingIdentifier" }

object AbsAsyncArrowBindingIdentifier extends AsyncArrowBindingIdentifier with AbsAST

object AsyncArrowBindingIdentifier {
  def apply(data: Json): AsyncArrowBindingIdentifier = AST(data) match {
    case Some(compressed) => AsyncArrowBindingIdentifier(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AsyncArrowBindingIdentifier = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingIdentifier(_)).get
        AsyncArrowBindingIdentifier0(x0, params, span)
    }
  }
}

case class AsyncArrowBindingIdentifier0(x0: BindingIdentifier, parserParams: List[Boolean], span: Span) extends AsyncArrowBindingIdentifier {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("BindingIdentifier", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
