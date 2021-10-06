package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait LiteralPropertyName extends AST { val kind: String = "LiteralPropertyName" }

object AbsLiteralPropertyName extends LiteralPropertyName with AbsAST

object LiteralPropertyName {
  def apply(data: Json): LiteralPropertyName = AST(data) match {
    case Some(compressed) => LiteralPropertyName(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): LiteralPropertyName = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Lexical(_)).get
        LiteralPropertyName0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(Lexical(_)).get
        LiteralPropertyName1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(Lexical(_)).get
        LiteralPropertyName2(x0, params, span)
    }
  }
}

case class LiteralPropertyName0(x0: Lexical, parserParams: List[Boolean], span: Span) extends LiteralPropertyName {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class LiteralPropertyName1(x0: Lexical, parserParams: List[Boolean], span: Span) extends LiteralPropertyName {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class LiteralPropertyName2(x0: Lexical, parserParams: List[Boolean], span: Span) extends LiteralPropertyName {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
