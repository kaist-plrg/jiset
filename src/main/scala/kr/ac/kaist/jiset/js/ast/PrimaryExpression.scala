package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait PrimaryExpression extends AST { val kind: String = "PrimaryExpression" }

case class PrimaryExpression0(parserParams: List[Boolean], span: Span) extends PrimaryExpression {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"this"
  }
}

case class PrimaryExpression1(x0: IdentifierReference, parserParams: List[Boolean], span: Span) extends PrimaryExpression {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("IdentifierReference", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PrimaryExpression2(x0: Literal, parserParams: List[Boolean], span: Span) extends PrimaryExpression {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("Literal", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PrimaryExpression3(x0: ArrayLiteral, parserParams: List[Boolean], span: Span) extends PrimaryExpression {
  x0.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ArrayLiteral", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PrimaryExpression4(x0: ObjectLiteral, parserParams: List[Boolean], span: Span) extends PrimaryExpression {
  x0.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ObjectLiteral", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PrimaryExpression5(x0: FunctionExpression, parserParams: List[Boolean], span: Span) extends PrimaryExpression {
  x0.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("FunctionExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PrimaryExpression6(x0: ClassExpression, parserParams: List[Boolean], span: Span) extends PrimaryExpression {
  x0.parent = Some(this)
  def idx: Int = 6
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ClassExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PrimaryExpression7(x0: GeneratorExpression, parserParams: List[Boolean], span: Span) extends PrimaryExpression {
  x0.parent = Some(this)
  def idx: Int = 7
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("GeneratorExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PrimaryExpression8(x0: AsyncFunctionExpression, parserParams: List[Boolean], span: Span) extends PrimaryExpression {
  x0.parent = Some(this)
  def idx: Int = 8
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("AsyncFunctionExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PrimaryExpression9(x0: AsyncGeneratorExpression, parserParams: List[Boolean], span: Span) extends PrimaryExpression {
  x0.parent = Some(this)
  def idx: Int = 9
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("AsyncGeneratorExpression", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PrimaryExpression10(x0: Lexical, parserParams: List[Boolean], span: Span) extends PrimaryExpression {
  x0.parent = Some(this)
  def idx: Int = 10
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PrimaryExpression11(x0: TemplateLiteral, parserParams: List[Boolean], span: Span) extends PrimaryExpression {
  x0.parent = Some(this)
  def idx: Int = 11
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("TemplateLiteral", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class PrimaryExpression12(x0: CoverParenthesizedExpressionAndArrowParameterList, parserParams: List[Boolean], span: Span) extends PrimaryExpression {
  x0.parent = Some(this)
  def idx: Int = 12
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("CoverParenthesizedExpressionAndArrowParameterList", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
