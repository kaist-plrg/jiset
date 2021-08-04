package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AsyncFunctionExpression extends AST { val kind: String = "AsyncFunctionExpression" }

object AsyncFunctionExpression {
  def apply(data: Json): AsyncFunctionExpression = AST(data) match {
    case Some(compressed) => AsyncFunctionExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AsyncFunctionExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingIdentifier(_))
        val x1 = subs(1).map(FormalParameters(_)).get
        val x2 = subs(2).map(AsyncFunctionBody(_)).get
        AsyncFunctionExpression0(x0, x1, x2, params, span)
    }
  }
}

case class AsyncFunctionExpression0(x3: Option[BindingIdentifier], x5: FormalParameters, x8: AsyncFunctionBody, parserParams: List[Boolean], span: Span) extends AsyncFunctionExpression {
  x3.foreach((m) => m.parent = Some(this))
  x5.parent = Some(this)
  x8.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x8, d(x5, d(x3, 0)))
  def fullList: List[(String, PureValue)] = l("AsyncFunctionBody", x8, l("FormalParameters", x5, l("Option[BindingIdentifier]", x3, Nil))).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"async function ${x3.getOrElse("")} ( $x5 ) { $x8 }"
  }
}
