package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ReturnStatement extends AST { val kind: String = "ReturnStatement" }

object ReturnStatement {
  def apply(data: Json): ReturnStatement = AST(data) match {
    case Some(compressed) => ReturnStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ReturnStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        ReturnStatement0(params, span)
      case 1 =>
        val x0 = subs(0).map(Expression(_)).get
        ReturnStatement1(x0, params, span)
    }
  }
}

case class ReturnStatement0(parserParams: List[Boolean], span: Span) extends ReturnStatement {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"return ;"
  }
}

case class ReturnStatement1(x2: Expression, parserParams: List[Boolean], span: Span) extends ReturnStatement {
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, 0)
  def fullList: List[(String, PureValue)] = l("Expression", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"return $x2 ;"
  }
}
