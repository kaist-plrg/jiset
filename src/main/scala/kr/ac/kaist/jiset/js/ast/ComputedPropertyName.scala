package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ComputedPropertyName extends AST { val kind: String = "ComputedPropertyName" }

object ComputedPropertyName {
  def apply(data: Json): ComputedPropertyName = AST(data) match {
    case Some(compressed) => ComputedPropertyName(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ComputedPropertyName = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(AssignmentExpression(_)).get
        ComputedPropertyName0(x0, params, span)
    }
  }
}

case class ComputedPropertyName0(x1: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ComputedPropertyName {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("AssignmentExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"[ $x1 ]"
  }
}
