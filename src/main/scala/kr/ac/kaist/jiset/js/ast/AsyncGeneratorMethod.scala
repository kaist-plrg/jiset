package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AsyncGeneratorMethod extends AST { val kind: String = "AsyncGeneratorMethod" }

object AbsAsyncGeneratorMethod extends AsyncGeneratorMethod with AbsAST

object AsyncGeneratorMethod {
  def apply(data: Json): AsyncGeneratorMethod = AST(data) match {
    case Some(compressed) => AsyncGeneratorMethod(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AsyncGeneratorMethod = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(PropertyName(_)).get
        val x1 = subs(1).map(UniqueFormalParameters(_)).get
        val x2 = subs(2).map(AsyncGeneratorBody(_)).get
        AsyncGeneratorMethod0(x0, x1, x2, params, span)
    }
  }
}

case class AsyncGeneratorMethod0(x3: PropertyName, x5: UniqueFormalParameters, x8: AsyncGeneratorBody, parserParams: List[Boolean], span: Span) extends AsyncGeneratorMethod {
  x3.parent = Some(this)
  x5.parent = Some(this)
  x8.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x8, d(x5, d(x3, 0)))
  def fullList: List[(String, PureValue)] = l("AsyncGeneratorBody", x8, l("UniqueFormalParameters", x5, l("PropertyName", x3, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"async * $x3 ( $x5 ) { $x8 }"
  }
}
