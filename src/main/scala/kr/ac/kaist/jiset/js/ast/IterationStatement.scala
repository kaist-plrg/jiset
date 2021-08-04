package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait IterationStatement extends AST { val kind: String = "IterationStatement" }

object IterationStatement {
  def apply(data: Json): IterationStatement = AST(data) match {
    case Some(compressed) => IterationStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): IterationStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(DoWhileStatement(_)).get
        IterationStatement0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(WhileStatement(_)).get
        IterationStatement1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(ForStatement(_)).get
        IterationStatement2(x0, params, span)
      case 3 =>
        val x0 = subs(0).map(ForInOfStatement(_)).get
        IterationStatement3(x0, params, span)
    }
  }
}

case class IterationStatement0(x0: DoWhileStatement, parserParams: List[Boolean], span: Span) extends IterationStatement {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("DoWhileStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class IterationStatement1(x0: WhileStatement, parserParams: List[Boolean], span: Span) extends IterationStatement {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("WhileStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class IterationStatement2(x0: ForStatement, parserParams: List[Boolean], span: Span) extends IterationStatement {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ForStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class IterationStatement3(x0: ForInOfStatement, parserParams: List[Boolean], span: Span) extends IterationStatement {
  x0.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ForInOfStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
