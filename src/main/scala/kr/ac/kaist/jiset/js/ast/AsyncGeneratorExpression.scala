package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait AsyncGeneratorExpression extends AST { val kind: String = "AsyncGeneratorExpression" }

object AbsAsyncGeneratorExpression extends AsyncGeneratorExpression with AbsAST

object AsyncGeneratorExpression {
  def apply(data: Json): AsyncGeneratorExpression = AST(data) match {
    case Some(compressed) => AsyncGeneratorExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): AsyncGeneratorExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingIdentifier(_))
        val x1 = subs(1).map(FormalParameters(_)).get
        val x2 = subs(2).map(AsyncGeneratorBody(_)).get
        AsyncGeneratorExpression0(x0, x1, x2, params, span)
    }
  }
}

case class AsyncGeneratorExpression0(x4: Option[BindingIdentifier], x6: FormalParameters, x9: AsyncGeneratorBody, parserParams: List[Boolean], span: Span) extends AsyncGeneratorExpression {
  x4.foreach((m) => m.parent = Some(this))
  x6.parent = Some(this)
  x9.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x9, d(x6, d(x4, 0)))
  def fullList: List[(String, PureValue)] = l("AsyncGeneratorBody", x9, l("FormalParameters", x6, l("Option[BindingIdentifier]", x4, Nil))).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"async function * ${x4.getOrElse("")} ( $x6 ) { $x9 }"
  }
}
