package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ImportedDefaultBinding extends AST { val kind: String = "ImportedDefaultBinding" }

object ImportedDefaultBinding {
  def apply(data: Json): ImportedDefaultBinding = AST(data) match {
    case Some(compressed) => ImportedDefaultBinding(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ImportedDefaultBinding = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ImportedBinding(_)).get
        ImportedDefaultBinding0(x0, params, span)
    }
  }
}

case class ImportedDefaultBinding0(x0: ImportedBinding, parserParams: List[Boolean], span: Span) extends ImportedDefaultBinding {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ImportedBinding", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
