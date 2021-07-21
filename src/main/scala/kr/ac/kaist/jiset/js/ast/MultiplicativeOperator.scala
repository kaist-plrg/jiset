package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait MultiplicativeOperator extends AST { val kind: String = "MultiplicativeOperator" }

object MultiplicativeOperator {
  def apply(data: Json): MultiplicativeOperator = AST(data) match {
    case Some(compressed) => MultiplicativeOperator(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): MultiplicativeOperator = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        MultiplicativeOperator0(params, span)
      case 1 =>
        MultiplicativeOperator1(params, span)
      case 2 =>
        MultiplicativeOperator2(params, span)
    }
  }
}

case class MultiplicativeOperator0(parserParams: List[Boolean], span: Span) extends MultiplicativeOperator {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"*"
  }
}

case class MultiplicativeOperator1(parserParams: List[Boolean], span: Span) extends MultiplicativeOperator {
  def idx: Int = 1
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"/"
  }
}

case class MultiplicativeOperator2(parserParams: List[Boolean], span: Span) extends MultiplicativeOperator {
  def idx: Int = 2
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"%"
  }
}
