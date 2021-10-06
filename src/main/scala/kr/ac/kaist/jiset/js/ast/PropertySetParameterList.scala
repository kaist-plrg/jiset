package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait PropertySetParameterList extends AST { val kind: String = "PropertySetParameterList" }

object AbsPropertySetParameterList extends PropertySetParameterList with AbsAST

object PropertySetParameterList {
  def apply(data: Json): PropertySetParameterList = AST(data) match {
    case Some(compressed) => PropertySetParameterList(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): PropertySetParameterList = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(FormalParameter(_)).get
        PropertySetParameterList0(x0, params, span)
    }
  }
}

case class PropertySetParameterList0(x0: FormalParameter, parserParams: List[Boolean], span: Span) extends PropertySetParameterList {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("FormalParameter", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
