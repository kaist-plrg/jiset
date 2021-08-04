package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ImportedBinding extends AST { val kind: String = "ImportedBinding" }

object ImportedBinding {
  def apply(data: Json): ImportedBinding = AST(data) match {
    case Some(compressed) => ImportedBinding(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ImportedBinding = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingIdentifier(_)).get
        ImportedBinding0(x0, params, span)
    }
  }
}

case class ImportedBinding0(x0: BindingIdentifier, parserParams: List[Boolean], span: Span) extends ImportedBinding {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("BindingIdentifier", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
