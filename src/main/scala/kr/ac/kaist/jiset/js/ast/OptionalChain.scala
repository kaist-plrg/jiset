package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait OptionalChain extends AST { val kind: String = "OptionalChain" }

object OptionalChain {
  def apply(data: Json): OptionalChain = AST(data) match {
    case Some(compressed) => OptionalChain(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): OptionalChain = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Arguments(_)).get
        OptionalChain0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(Expression(_)).get
        OptionalChain1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(Lexical(_)).get
        OptionalChain2(x0, params, span)
      case 3 =>
        val x0 = subs(0).map(TemplateLiteral(_)).get
        OptionalChain3(x0, params, span)
      case 4 =>
        val x0 = subs(0).map(OptionalChain(_)).get
        val x1 = subs(1).map(Arguments(_)).get
        OptionalChain4(x0, x1, params, span)
      case 5 =>
        val x0 = subs(0).map(OptionalChain(_)).get
        val x1 = subs(1).map(Expression(_)).get
        OptionalChain5(x0, x1, params, span)
      case 6 =>
        val x0 = subs(0).map(OptionalChain(_)).get
        val x1 = subs(1).map(Lexical(_)).get
        OptionalChain6(x0, x1, params, span)
      case 7 =>
        val x0 = subs(0).map(OptionalChain(_)).get
        val x1 = subs(1).map(TemplateLiteral(_)).get
        OptionalChain7(x0, x1, params, span)
    }
  }
}

case class OptionalChain0(x1: Arguments, parserParams: List[Boolean], span: Span) extends OptionalChain {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Arguments", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"?. $x1"
  }
}

case class OptionalChain1(x2: Expression, parserParams: List[Boolean], span: Span) extends OptionalChain {
  x2.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("Expression", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"?. [ $x2 ]"
  }
}

case class OptionalChain2(x1: Lexical, parserParams: List[Boolean], span: Span) extends OptionalChain {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Lexical", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"?. $x1"
  }
}

case class OptionalChain3(x1: TemplateLiteral, parserParams: List[Boolean], span: Span) extends OptionalChain {
  x1.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("TemplateLiteral", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"?. $x1"
  }
}

case class OptionalChain4(x0: OptionalChain, x1: Arguments, parserParams: List[Boolean], span: Span) extends OptionalChain {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("Arguments", x1, l("OptionalChain", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}

case class OptionalChain5(x0: OptionalChain, x2: Expression, parserParams: List[Boolean], span: Span) extends OptionalChain {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("Expression", x2, l("OptionalChain", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 [ $x2 ]"
  }
}

case class OptionalChain6(x0: OptionalChain, x2: Lexical, parserParams: List[Boolean], span: Span) extends OptionalChain {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 6
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("Lexical", x2, l("OptionalChain", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 . $x2"
  }
}

case class OptionalChain7(x0: OptionalChain, x1: TemplateLiteral, parserParams: List[Boolean], span: Span) extends OptionalChain {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 7
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, Value)] = l("TemplateLiteral", x1, l("OptionalChain", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
