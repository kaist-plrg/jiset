package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait EqualityExpression extends AST { val kind: String = "EqualityExpression" }

object EqualityExpression {
  def apply(data: Json): EqualityExpression = AST(data) match {
    case Some(compressed) => EqualityExpression(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): EqualityExpression = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(RelationalExpression(_)).get
        EqualityExpression0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(EqualityExpression(_)).get
        val x1 = subs(1).map(RelationalExpression(_)).get
        EqualityExpression1(x0, x1, params, span)
      case 2 =>
        val x0 = subs(0).map(EqualityExpression(_)).get
        val x1 = subs(1).map(RelationalExpression(_)).get
        EqualityExpression2(x0, x1, params, span)
      case 3 =>
        val x0 = subs(0).map(EqualityExpression(_)).get
        val x1 = subs(1).map(RelationalExpression(_)).get
        EqualityExpression3(x0, x1, params, span)
      case 4 =>
        val x0 = subs(0).map(EqualityExpression(_)).get
        val x1 = subs(1).map(RelationalExpression(_)).get
        EqualityExpression4(x0, x1, params, span)
    }
  }
}

case class EqualityExpression0(x0: RelationalExpression, parserParams: List[Boolean], span: Span) extends EqualityExpression {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("RelationalExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class EqualityExpression1(x0: EqualityExpression, x2: RelationalExpression, parserParams: List[Boolean], span: Span) extends EqualityExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("RelationalExpression", x2, l("EqualityExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 == $x2"
  }
}

case class EqualityExpression2(x0: EqualityExpression, x2: RelationalExpression, parserParams: List[Boolean], span: Span) extends EqualityExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("RelationalExpression", x2, l("EqualityExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 != $x2"
  }
}

case class EqualityExpression3(x0: EqualityExpression, x2: RelationalExpression, parserParams: List[Boolean], span: Span) extends EqualityExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("RelationalExpression", x2, l("EqualityExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 === $x2"
  }
}

case class EqualityExpression4(x0: EqualityExpression, x2: RelationalExpression, parserParams: List[Boolean], span: Span) extends EqualityExpression {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("RelationalExpression", x2, l("EqualityExpression", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 !== $x2"
  }
}
