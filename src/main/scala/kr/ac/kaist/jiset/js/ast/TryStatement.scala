package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait TryStatement extends AST { val kind: String = "TryStatement" }

object TryStatement {
  def apply(data: Json): TryStatement = AST(data) match {
    case Some(compressed) => TryStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): TryStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Block(_)).get
        val x1 = subs(1).map(Catch(_)).get
        TryStatement0(x0, x1, params, span)
      case 1 =>
        val x0 = subs(0).map(Block(_)).get
        val x1 = subs(1).map(Finally(_)).get
        TryStatement1(x0, x1, params, span)
      case 2 =>
        val x0 = subs(0).map(Block(_)).get
        val x1 = subs(1).map(Catch(_)).get
        val x2 = subs(2).map(Finally(_)).get
        TryStatement2(x0, x1, x2, params, span)
    }
  }
}

case class TryStatement0(x1: Block, x2: Catch, parserParams: List[Boolean], span: Span) extends TryStatement {
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, Value)] = l("Catch", x2, l("Block", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"try $x1 $x2"
  }
}

case class TryStatement1(x1: Block, x2: Finally, parserParams: List[Boolean], span: Span) extends TryStatement {
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, Value)] = l("Finally", x2, l("Block", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"try $x1 $x2"
  }
}

case class TryStatement2(x1: Block, x2: Catch, x3: Finally, parserParams: List[Boolean], span: Span) extends TryStatement {
  x1.parent = Some(this)
  x2.parent = Some(this)
  x3.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x3, d(x2, d(x1, 0)))
  def fullList: List[(String, Value)] = l("Finally", x3, l("Catch", x2, l("Block", x1, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"try $x1 $x2 $x3"
  }
}
