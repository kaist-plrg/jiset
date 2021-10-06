package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait FormalParameters extends AST { val kind: String = "FormalParameters" }

object AbsFormalParameters extends FormalParameters with AbsAST

object FormalParameters {
  def apply(data: Json): FormalParameters = AST(data) match {
    case Some(compressed) => FormalParameters(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): FormalParameters = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        FormalParameters0(params, span)
      case 1 =>
        val x0 = subs(0).map(FunctionRestParameter(_)).get
        FormalParameters1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(FormalParameterList(_)).get
        FormalParameters2(x0, params, span)
      case 3 =>
        val x0 = subs(0).map(FormalParameterList(_)).get
        FormalParameters3(x0, params, span)
      case 4 =>
        val x0 = subs(0).map(FormalParameterList(_)).get
        val x1 = subs(1).map(FunctionRestParameter(_)).get
        FormalParameters4(x0, x1, params, span)
    }
  }
}

case class FormalParameters0(parserParams: List[Boolean], span: Span) extends FormalParameters {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s""
  }
}

case class FormalParameters1(x0: FunctionRestParameter, parserParams: List[Boolean], span: Span) extends FormalParameters {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("FunctionRestParameter", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class FormalParameters2(x0: FormalParameterList, parserParams: List[Boolean], span: Span) extends FormalParameters {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("FormalParameterList", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class FormalParameters3(x0: FormalParameterList, parserParams: List[Boolean], span: Span) extends FormalParameters {
  x0.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("FormalParameterList", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 ,"
  }
}

case class FormalParameters4(x0: FormalParameterList, x2: FunctionRestParameter, parserParams: List[Boolean], span: Span) extends FormalParameters {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("FunctionRestParameter", x2, l("FormalParameterList", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
