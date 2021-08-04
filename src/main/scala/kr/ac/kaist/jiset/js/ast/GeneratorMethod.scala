package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait GeneratorMethod extends AST { val kind: String = "GeneratorMethod" }

object GeneratorMethod {
  def apply(data: Json): GeneratorMethod = AST(data) match {
    case Some(compressed) => GeneratorMethod(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): GeneratorMethod = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(PropertyName(_)).get
        val x1 = subs(1).map(UniqueFormalParameters(_)).get
        val x2 = subs(2).map(GeneratorBody(_)).get
        GeneratorMethod0(x0, x1, x2, params, span)
    }
  }
}

case class GeneratorMethod0(x1: PropertyName, x3: UniqueFormalParameters, x6: GeneratorBody, parserParams: List[Boolean], span: Span) extends GeneratorMethod {
  x1.parent = Some(this)
  x3.parent = Some(this)
  x6.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x6, d(x3, d(x1, 0)))
  def fullList: List[(String, PureValue)] = l("GeneratorBody", x6, l("UniqueFormalParameters", x3, l("PropertyName", x1, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"* $x1 ( $x3 ) { $x6 }"
  }
}
