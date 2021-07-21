package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait Initializer extends AST { val kind: String = "Initializer" }

object Initializer {
  def apply(data: Json): Initializer = AST(data) match {
    case Some(compressed) => Initializer(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): Initializer = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(AssignmentExpression(_)).get
        Initializer0(x0, params, span)
    }
  }
}

case class Initializer0(x1: AssignmentExpression, parserParams: List[Boolean], span: Span) extends Initializer {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("AssignmentExpression", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"= $x1"
  }
}
