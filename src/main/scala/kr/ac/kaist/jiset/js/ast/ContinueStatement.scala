package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ContinueStatement extends AST { val kind: String = "ContinueStatement" }

object AbsContinueStatement extends ContinueStatement with AbsAST

object ContinueStatement {
  def apply(data: Json): ContinueStatement = AST(data) match {
    case Some(compressed) => ContinueStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ContinueStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        ContinueStatement0(params, span)
      case 1 =>
        val x0 = subs(0).map(LabelIdentifier(_)).get
        ContinueStatement1(x0, params, span)
    }
  }
}

case class ContinueStatement0(parserParams: List[Boolean], span: Span) extends ContinueStatement {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, PureValue)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"continue ;"
  }
}

case class ContinueStatement1(x2: LabelIdentifier, parserParams: List[Boolean], span: Span) extends ContinueStatement {
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, 0)
  def fullList: List[(String, PureValue)] = l("LabelIdentifier", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"continue $x2 ;"
  }
}
