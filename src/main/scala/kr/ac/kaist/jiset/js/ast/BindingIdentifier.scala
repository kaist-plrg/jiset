package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait BindingIdentifier extends AST { val kind: String = "BindingIdentifier" }

object AbsBindingIdentifier extends BindingIdentifier with AbsAST

object BindingIdentifier {
  def apply(data: Json): BindingIdentifier = AST(data) match {
    case Some(compressed) => BindingIdentifier(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): BindingIdentifier = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Identifier(_)).get
        BindingIdentifier0(x0, params, span)
      case 1 =>
        BindingIdentifier1(params, span)
      case 2 =>
        BindingIdentifier2(params, span)
    }
  }
}

case class BindingIdentifier0(x0: Identifier, parserParams: List[Boolean], span: Span) extends BindingIdentifier {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("Identifier", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class BindingIdentifier1(parserParams: List[Boolean], span: Span) extends BindingIdentifier {
  def idx: Int = 1
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"yield"
  }
}

case class BindingIdentifier2(parserParams: List[Boolean], span: Span) extends BindingIdentifier {
  def idx: Int = 2
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"await"
  }
}
