package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait BlockStatement extends AST { val kind: String = "BlockStatement" }

object AbsBlockStatement extends BlockStatement with AbsAST

object BlockStatement {
  def apply(data: Json): BlockStatement = AST(data) match {
    case Some(compressed) => BlockStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): BlockStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Block(_)).get
        BlockStatement0(x0, params, span)
    }
  }
}

case class BlockStatement0(x0: Block, parserParams: List[Boolean], span: Span) extends BlockStatement {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("Block", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
