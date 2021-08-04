package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait FormalParameterList extends AST { val kind: String = "FormalParameterList" }

object FormalParameterList {
  def apply(data: Json): FormalParameterList = AST(data) match {
    case Some(compressed) => FormalParameterList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): FormalParameterList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(FormalParameter(_)).get
        FormalParameterList0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(FormalParameterList(_)).get
        val x1 = subs(1).map(FormalParameter(_)).get
        FormalParameterList1(x0, x1, params, span)
    }
  }
}

case class FormalParameterList0(x0: FormalParameter, parserParams: List[Boolean], span: Span) extends FormalParameterList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("FormalParameter", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class FormalParameterList1(x0: FormalParameterList, x2: FormalParameter, parserParams: List[Boolean], span: Span) extends FormalParameterList {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("FormalParameter", x2, l("FormalParameterList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
