package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ImportCall extends AST { val kind: String = "ImportCall" }

object ImportCall {
  def apply(data: Json): ImportCall = AST(data) match {
    case Some(compressed) => ImportCall(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ImportCall = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(AssignmentExpression(_)).get
        ImportCall0(x0, params, span)
    }
  }
}

case class ImportCall0(x2: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ImportCall {
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("AssignmentExpression", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"import ( $x2 )"
  }
}
