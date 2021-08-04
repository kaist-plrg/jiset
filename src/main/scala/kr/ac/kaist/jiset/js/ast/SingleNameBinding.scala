package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait SingleNameBinding extends AST { val kind: String = "SingleNameBinding" }

object SingleNameBinding {
  def apply(data: Json): SingleNameBinding = AST(data) match {
    case Some(compressed) => SingleNameBinding(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): SingleNameBinding = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingIdentifier(_)).get
        val x1 = subs(1).map(Initializer(_))
        SingleNameBinding0(x0, x1, params, span)
    }
  }
}

case class SingleNameBinding0(x0: BindingIdentifier, x1: Option[Initializer], parserParams: List[Boolean], span: Span) extends SingleNameBinding {
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
