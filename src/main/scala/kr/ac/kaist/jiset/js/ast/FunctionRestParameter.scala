package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait FunctionRestParameter extends AST { val kind: String = "FunctionRestParameter" }

object AbsFunctionRestParameter extends FunctionRestParameter with AbsAST

object FunctionRestParameter {
  def apply(data: Json): FunctionRestParameter = AST(data) match {
    case Some(compressed) => FunctionRestParameter(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): FunctionRestParameter = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BindingRestElement(_)).get
        FunctionRestParameter0(x0, params, span)
    }
  }
}

case class FunctionRestParameter0(x0: BindingRestElement, parserParams: List[Boolean], span: Span) extends FunctionRestParameter {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("BindingRestElement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
