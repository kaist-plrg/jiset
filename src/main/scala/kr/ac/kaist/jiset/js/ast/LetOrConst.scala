package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait LetOrConst extends AST { val kind: String = "LetOrConst" }

object AbsLetOrConst extends LetOrConst with AbsAST

object LetOrConst {
  def apply(data: Json): LetOrConst = AST(data) match {
    case Some(compressed) => LetOrConst(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): LetOrConst = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        LetOrConst0(params, span)
      case 1 =>
        LetOrConst1(params, span)
    }
  }
}

case class LetOrConst0(parserParams: List[Boolean], span: Span) extends LetOrConst {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"let"
  }
}

case class LetOrConst1(parserParams: List[Boolean], span: Span) extends LetOrConst {
  def idx: Int = 1
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"const"
  }
}
