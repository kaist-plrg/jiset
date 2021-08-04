package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait Identifier extends AST { val kind: String = "Identifier" }

object Identifier {
  def apply(data: Json): Identifier = AST(data) match {
    case Some(compressed) => Identifier(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): Identifier = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Lexical(_)).get
        Identifier0(x0, params, span)
    }
  }
}

case class Identifier0(x0: Lexical, parserParams: List[Boolean], span: Span) extends Identifier {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
