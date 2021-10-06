package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait NewTarget extends AST { val kind: String = "NewTarget" }

object AbsNewTarget extends NewTarget with AbsAST

object NewTarget {
  def apply(data: Json): NewTarget = AST(data) match {
    case Some(compressed) => NewTarget(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): NewTarget = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        NewTarget0(params, span)
    }
  }
}

case class NewTarget0(parserParams: List[Boolean], span: Span) extends NewTarget {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"new . target"
  }
}
