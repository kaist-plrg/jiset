package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait Statement extends AST { val kind: String = "Statement" }

object AbsStatement extends Statement with AbsAST

object Statement {
  def apply(data: Json): Statement = AST(data) match {
    case Some(compressed) => Statement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): Statement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(BlockStatement(_)).get
        Statement0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(VariableStatement(_)).get
        Statement1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(EmptyStatement(_)).get
        Statement2(x0, params, span)
      case 3 =>
        val x0 = subs(0).map(ExpressionStatement(_)).get
        Statement3(x0, params, span)
      case 4 =>
        val x0 = subs(0).map(IfStatement(_)).get
        Statement4(x0, params, span)
      case 5 =>
        val x0 = subs(0).map(BreakableStatement(_)).get
        Statement5(x0, params, span)
      case 6 =>
        val x0 = subs(0).map(ContinueStatement(_)).get
        Statement6(x0, params, span)
      case 7 =>
        val x0 = subs(0).map(BreakStatement(_)).get
        Statement7(x0, params, span)
      case 8 =>
        val x0 = subs(0).map(ReturnStatement(_)).get
        Statement8(x0, params, span)
      case 9 =>
        val x0 = subs(0).map(WithStatement(_)).get
        Statement9(x0, params, span)
      case 10 =>
        val x0 = subs(0).map(LabelledStatement(_)).get
        Statement10(x0, params, span)
      case 11 =>
        val x0 = subs(0).map(ThrowStatement(_)).get
        Statement11(x0, params, span)
      case 12 =>
        val x0 = subs(0).map(TryStatement(_)).get
        Statement12(x0, params, span)
      case 13 =>
        val x0 = subs(0).map(DebuggerStatement(_)).get
        Statement13(x0, params, span)
    }
  }
}

case class Statement0(x0: BlockStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("BlockStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement1(x0: VariableStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("VariableStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement2(x0: EmptyStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("EmptyStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement3(x0: ExpressionStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ExpressionStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement4(x0: IfStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("IfStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement5(x0: BreakableStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("BreakableStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement6(x0: ContinueStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 6
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ContinueStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement7(x0: BreakStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 7
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("BreakStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement8(x0: ReturnStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 8
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ReturnStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement9(x0: WithStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 9
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("WithStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement10(x0: LabelledStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 10
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("LabelledStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement11(x0: ThrowStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 11
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ThrowStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement12(x0: TryStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 12
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("TryStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement13(x0: DebuggerStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 13
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("DebuggerStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
