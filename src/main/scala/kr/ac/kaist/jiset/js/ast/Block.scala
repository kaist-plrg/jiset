package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait Block extends AST { val kind: String = "Block" }

object Block {
  def apply(data: Json): Block = AST(data) match {
    case Some(compressed) => Block(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): Block = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(StatementList(_))
        Block0(x0, params, span)
    }
  }
}

case class Block0(x1: Option[StatementList], parserParams: List[Boolean], span: Span) extends Block {
  x1.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Option[StatementList]", x1, Nil).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"{ ${x1.getOrElse("")} }"
  }
}
