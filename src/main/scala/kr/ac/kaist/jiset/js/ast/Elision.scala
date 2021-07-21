package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait Elision extends AST { val kind: String = "Elision" }

object Elision {
  def apply(data: Json): Elision = AST(data) match {
    case Some(compressed) => Elision(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): Elision = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        Elision0(params, span)
      case 1 =>
        val x0 = subs(0).map(Elision(_)).get
        Elision1(x0, params, span)
    }
  }
}

case class Elision0(parserParams: List[Boolean], span: Span) extends Elision {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s","
  }
}

case class Elision1(x0: Elision, parserParams: List[Boolean], span: Span) extends Elision {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("Elision", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ,"
  }
}
