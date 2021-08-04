package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AsyncGeneratorBody extends AST { val kind: String = "AsyncGeneratorBody" }

object AsyncGeneratorBody {
  def apply(data: Json): AsyncGeneratorBody = AST(data) match {
    case Some(compressed) => AsyncGeneratorBody(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AsyncGeneratorBody = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(FunctionBody(_)).get
        AsyncGeneratorBody0(x0, params, span)
    }
  }
}

case class AsyncGeneratorBody0(x0: FunctionBody, parserParams: List[Boolean], span: Span) extends AsyncGeneratorBody {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("FunctionBody", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
