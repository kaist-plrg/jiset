package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait FunctionExpression extends AST { val kind: String = "FunctionExpression" }

object FunctionExpression {
  def apply(data: Json): FunctionExpression = AST(data) match {
    case Some(compressed) => FunctionExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): FunctionExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingIdentifier(_))
        val x1 = subs(1).map(FormalParameters(_)).get
        val x2 = subs(2).map(FunctionBody(_)).get
        FunctionExpression0(x0, x1, x2, params, span)
    }
  }
}

case class FunctionExpression0(x1: Option[BindingIdentifier], x3: FormalParameters, x6: FunctionBody, parserParams: List[Boolean], span: Span) extends FunctionExpression {
  x1.foreach((m) => m.parent = Some(this))
  x3.parent = Some(this)
  x6.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x6, d(x3, d(x1, 0)))
  def fullList: List[(String, PureValue)] = l("FunctionBody", x6, l("FormalParameters", x3, l("Option[BindingIdentifier]", x1, Nil))).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"function ${x1.getOrElse("")} ( $x3 ) { $x6 }"
  }
}
