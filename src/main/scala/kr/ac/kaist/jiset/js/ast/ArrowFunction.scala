package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ArrowFunction extends AST { val kind: String = "ArrowFunction" }

object ArrowFunction {
  def apply(data: Json): ArrowFunction = AST(data) match {
    case Some(compressed) => ArrowFunction(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ArrowFunction = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ArrowParameters(_)).get
        val x1 = subs(1).map(ConciseBody(_)).get
        ArrowFunction0(x0, x1, params, span)
    }
  }
}

case class ArrowFunction0(x0: ArrowParameters, x3: ConciseBody, parserParams: List[Boolean], span: Span) extends ArrowFunction {
  x0.parent = Some(this)
  x3.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x3, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("ConciseBody", x3, l("ArrowParameters", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 => $x3"
  }
}
