package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait TemplateLiteral extends AST { val kind: String = "TemplateLiteral" }

object TemplateLiteral {
  def apply(data: Json): TemplateLiteral = AST(data) match {
    case Some(compressed) => TemplateLiteral(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): TemplateLiteral = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Lexical(_)).get
        TemplateLiteral0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(SubstitutionTemplate(_)).get
        TemplateLiteral1(x0, params, span)
    }
  }
}

case class TemplateLiteral0(x0: Lexical, parserParams: List[Boolean], span: Span) extends TemplateLiteral {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class TemplateLiteral1(x0: SubstitutionTemplate, parserParams: List[Boolean], span: Span) extends TemplateLiteral {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("SubstitutionTemplate", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
