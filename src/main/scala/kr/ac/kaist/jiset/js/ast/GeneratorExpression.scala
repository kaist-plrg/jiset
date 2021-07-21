package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait GeneratorExpression extends AST { val kind: String = "GeneratorExpression" }

object GeneratorExpression {
  def apply(data: Json): GeneratorExpression = AST(data) match {
    case Some(compressed) => GeneratorExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): GeneratorExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingIdentifier(_))
        val x1 = subs(1).map(FormalParameters(_)).get
        val x2 = subs(2).map(GeneratorBody(_)).get
        GeneratorExpression0(x0, x1, x2, params, span)
    }
  }
}

case class GeneratorExpression0(x2: Option[BindingIdentifier], x4: FormalParameters, x7: GeneratorBody, parserParams: List[Boolean], span: Span) extends GeneratorExpression {
  x2.foreach((m) => m.parent = Some(this))
  x4.parent = Some(this)
  x7.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x7, d(x4, d(x2, 0)))
  def fullList: List[(String, Value)] = l("GeneratorBody", x7, l("FormalParameters", x4, l("Option[BindingIdentifier]", x2, Nil))).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"function * ${x2.getOrElse("")} ( $x4 ) { $x7 }"
  }
}
