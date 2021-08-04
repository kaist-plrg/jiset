package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait EmptyStatement extends AST { val kind: String = "EmptyStatement" }

object EmptyStatement {
  def apply(data: Json): EmptyStatement = AST(data) match {
    case Some(compressed) => EmptyStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): EmptyStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        EmptyStatement0(params, span)
    }
  }
}

case class EmptyStatement0(parserParams: List[Boolean], span: Span) extends EmptyStatement {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s";"
  }
}
