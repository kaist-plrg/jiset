package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait PipelineExpression extends AST { val kind: String = "PipelineExpression" }

object PipelineExpression {
  def apply(data: Json): PipelineExpression = AST(data) match {
    case Some(compressed) => PipelineExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): PipelineExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(LogicalORExpression(_)).get
        PipelineExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(PipelineExpression(_)).get
        val x1 = subs(1).map(LogicalORExpression(_)).get
        PipelineExpression1(x0, x1, params, span)
    }
  }
}

case class PipelineExpression0(x0: LogicalORExpression, parserParams: List[Boolean], span: Span) extends PipelineExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("LogicalORExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PipelineExpression1(x0: PipelineExpression, x2: LogicalORExpression, parserParams: List[Boolean], span: Span) extends PipelineExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("LogicalORExpression", x2, l("PipelineExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 |> $x2"
  }
}
