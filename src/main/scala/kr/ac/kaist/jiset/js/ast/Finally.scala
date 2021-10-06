package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait Finally extends AST { val kind: String = "Finally" }

object AbsFinally extends Finally with AbsAST

object Finally {
  def apply(data: Json): Finally = AST(data) match {
    case Some(compressed) => Finally(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): Finally = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Block(_)).get
        Finally0(x0, params, span)
    }
  }
}

case class Finally0(x1: Block, parserParams: List[Boolean], span: Span) extends Finally {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("Block", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"finally $x1"
  }
}
