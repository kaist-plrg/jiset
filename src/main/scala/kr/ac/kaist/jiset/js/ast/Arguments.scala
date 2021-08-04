package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait Arguments extends AST { val kind: String = "Arguments" }

object Arguments {
  def apply(data: Json): Arguments = AST(data) match {
    case Some(compressed) => Arguments(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): Arguments = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        Arguments0(params, span)
      case 1 =>
        val x0 = subs(0).map(ArgumentList(_)).get
        Arguments1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(ArgumentList(_)).get
        Arguments2(x0, params, span)
    }
  }
}

case class Arguments0(parserParams: List[Boolean], span: Span) extends Arguments {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( )"
  }
}

case class Arguments1(x1: ArgumentList, parserParams: List[Boolean], span: Span) extends Arguments {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("ArgumentList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( $x1 )"
  }
}

case class Arguments2(x1: ArgumentList, parserParams: List[Boolean], span: Span) extends Arguments {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("ArgumentList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"( $x1 , )"
  }
}
