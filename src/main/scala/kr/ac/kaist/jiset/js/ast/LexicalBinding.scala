package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait LexicalBinding extends AST { val kind: String = "LexicalBinding" }

object AbsLexicalBinding extends LexicalBinding with AbsAST

object LexicalBinding {
  def apply(data: Json): LexicalBinding = AST(data) match {
    case Some(compressed) => LexicalBinding(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): LexicalBinding = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingIdentifier(_)).get
        val x1 = subs(1).map(Initializer(_))
        LexicalBinding0(x0, x1, params, span)
      case 1 =>
        val x0 = subs(0).map(BindingPattern(_)).get
        val x1 = subs(1).map(Initializer(_)).get
        LexicalBinding1(x0, x1, params, span)
    }
  }
}

case class LexicalBinding0(x0: BindingIdentifier, x1: Option[Initializer], parserParams: List[Boolean], span: Span) extends LexicalBinding {
  x0.parent = Some(this)
  x1.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("Option[Initializer]", x1, l("BindingIdentifier", x0, Nil)).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"$x0 ${x1.getOrElse("")}"
  }
}

case class LexicalBinding1(x0: BindingPattern, x1: Initializer, parserParams: List[Boolean], span: Span) extends LexicalBinding {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("Initializer", x1, l("BindingPattern", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
