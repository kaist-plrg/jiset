package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait CoverInitializedName extends AST { val kind: String = "CoverInitializedName" }

object CoverInitializedName {
  def apply(data: Json): CoverInitializedName = AST(data) match {
    case Some(compressed) => CoverInitializedName(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): CoverInitializedName = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(IdentifierReference(_)).get
        val x1 = subs(1).map(Initializer(_)).get
        CoverInitializedName0(x0, x1, params, span)
    }
  }
}

case class CoverInitializedName0(x0: IdentifierReference, x1: Initializer, parserParams: List[Boolean], span: Span) extends CoverInitializedName {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("Initializer", x1, l("IdentifierReference", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
