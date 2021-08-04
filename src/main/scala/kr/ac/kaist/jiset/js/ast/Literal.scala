package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait Literal extends AST { val kind: String = "Literal" }

object Literal {
  def apply(data: Json): Literal = AST(data) match {
    case Some(compressed) => Literal(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): Literal = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Lexical(_)).get
        Literal0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(Lexical(_)).get
        Literal1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(Lexical(_)).get
        Literal2(x0, params, span)
      case 3 =>
        val x0 = subs(0).map(Lexical(_)).get
        Literal3(x0, params, span)
    }
  }
}

case class Literal0(x0: Lexical, parserParams: List[Boolean], span: Span) extends Literal {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Literal1(x0: Lexical, parserParams: List[Boolean], span: Span) extends Literal {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Literal2(x0: Lexical, parserParams: List[Boolean], span: Span) extends Literal {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Literal3(x0: Lexical, parserParams: List[Boolean], span: Span) extends Literal {
  x0.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
