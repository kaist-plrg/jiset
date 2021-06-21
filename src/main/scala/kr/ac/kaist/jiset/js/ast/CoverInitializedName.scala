package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait CoverInitializedName extends AST { val kind: String = "CoverInitializedName" }

case class CoverInitializedName0(x0: IdentifierReference, x1: Initializer, parserParams: List[Boolean], span: Span) extends CoverInitializedName {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("Initializer", x1, l("IdentifierReference", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
