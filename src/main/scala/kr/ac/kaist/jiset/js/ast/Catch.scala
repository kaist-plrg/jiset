package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait Catch extends AST { val kind: String = "Catch" }

object Catch {
  def apply(data: Json): Catch = AST(data) match {
    case Some(compressed) => Catch(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): Catch = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(CatchParameter(_)).get
        val x1 = subs(1).map(Block(_)).get
        Catch0(x0, x1, params, span)
      case 1 =>
        val x0 = subs(0).map(Block(_)).get
        Catch1(x0, params, span)
    }
  }
}

case class Catch0(x2: CatchParameter, x4: Block, parserParams: List[Boolean], span: Span) extends Catch {
  x2.parent = Some(this)
  x4.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x4, d(x2, 0))
  def fullList: List[(String, Value)] = l("Block", x4, l("CatchParameter", x2, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"catch ( $x2 ) $x4"
  }
}

case class Catch1(x1: Block, parserParams: List[Boolean], span: Span) extends Catch {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Block", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"catch $x1"
  }
}
