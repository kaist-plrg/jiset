package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AsyncMethod extends AST { val kind: String = "AsyncMethod" }

object AbsAsyncMethod extends AsyncMethod with AbsAST

object AsyncMethod {
  def apply(data: Json): AsyncMethod = AST(data) match {
    case Some(compressed) => AsyncMethod(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AsyncMethod = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(PropertyName(_)).get
        val x1 = subs(1).map(UniqueFormalParameters(_)).get
        val x2 = subs(2).map(AsyncFunctionBody(_)).get
        AsyncMethod0(x0, x1, x2, params, span)
    }
  }
}

case class AsyncMethod0(x2: PropertyName, x4: UniqueFormalParameters, x7: AsyncFunctionBody, parserParams: List[Boolean], span: Span) extends AsyncMethod {
  x2.parent = Some(this)
  x4.parent = Some(this)
  x7.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x7, d(x4, d(x2, 0)))
  def fullList: List[(String, PureValue)] = l("AsyncFunctionBody", x7, l("UniqueFormalParameters", x4, l("PropertyName", x2, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"async $x2 ( $x4 ) { $x7 }"
  }
}
