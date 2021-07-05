package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait Statement extends AST { val kind: String = "Statement" }

case class Statement0(x0: BlockStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("BlockStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement1(x0: VariableStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("VariableStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement2(x0: EmptyStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("EmptyStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement3(x0: ExpressionStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ExpressionStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement4(x0: IfStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("IfStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement5(x0: BreakableStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("BreakableStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement6(x0: ContinueStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 6
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ContinueStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement7(x0: BreakStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 7
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("BreakStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement8(x0: ReturnStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 8
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ReturnStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement9(x0: WithStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 9
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("WithStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement10(x0: LabelledStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 10
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("LabelledStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement11(x0: ThrowStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 11
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ThrowStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement12(x0: TryStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 12
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("TryStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Statement13(x0: DebuggerStatement, parserParams: List[Boolean], span: Span) extends Statement {
  x0.parent = Some(this)
  def idx: Int = 13
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("DebuggerStatement", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
