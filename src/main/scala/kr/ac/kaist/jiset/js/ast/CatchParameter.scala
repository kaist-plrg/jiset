package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait CatchParameter extends AST { val kind: String = "CatchParameter" }

object AbsCatchParameter extends CatchParameter with AbsAST

object CatchParameter {
  def apply(data: Json): CatchParameter = AST(data) match {
    case Some(compressed) => CatchParameter(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): CatchParameter = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingIdentifier(_)).get
        CatchParameter0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(BindingPattern(_)).get
        CatchParameter1(x0, params, span)
    }
  }
}

case class CatchParameter0(x0: BindingIdentifier, parserParams: List[Boolean], span: Span) extends CatchParameter {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("BindingIdentifier", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class CatchParameter1(x0: BindingPattern, parserParams: List[Boolean], span: Span) extends CatchParameter {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("BindingPattern", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
