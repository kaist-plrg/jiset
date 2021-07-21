package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ForInOfStatement extends AST { val kind: String = "ForInOfStatement" }

object ForInOfStatement {
  def apply(data: Json): ForInOfStatement = AST(data) match {
    case Some(compressed) => ForInOfStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ForInOfStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(LeftHandSideExpression(_)).get
        val x1 = subs(1).map(Expression(_)).get
        val x2 = subs(2).map(Statement(_)).get
        ForInOfStatement0(x0, x1, x2, params, span)
      case 1 =>
        val x0 = subs(0).map(ForBinding(_)).get
        val x1 = subs(1).map(Expression(_)).get
        val x2 = subs(2).map(Statement(_)).get
        ForInOfStatement1(x0, x1, x2, params, span)
      case 2 =>
        val x0 = subs(0).map(ForDeclaration(_)).get
        val x1 = subs(1).map(Expression(_)).get
        val x2 = subs(2).map(Statement(_)).get
        ForInOfStatement2(x0, x1, x2, params, span)
      case 3 =>
        val x0 = subs(0).map(LeftHandSideExpression(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        val x2 = subs(2).map(Statement(_)).get
        ForInOfStatement3(x0, x1, x2, params, span)
      case 4 =>
        val x0 = subs(0).map(ForBinding(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        val x2 = subs(2).map(Statement(_)).get
        ForInOfStatement4(x0, x1, x2, params, span)
      case 5 =>
        val x0 = subs(0).map(ForDeclaration(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        val x2 = subs(2).map(Statement(_)).get
        ForInOfStatement5(x0, x1, x2, params, span)
      case 6 =>
        val x0 = subs(0).map(LeftHandSideExpression(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        val x2 = subs(2).map(Statement(_)).get
        ForInOfStatement6(x0, x1, x2, params, span)
      case 7 =>
        val x0 = subs(0).map(ForBinding(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        val x2 = subs(2).map(Statement(_)).get
        ForInOfStatement7(x0, x1, x2, params, span)
      case 8 =>
        val x0 = subs(0).map(ForDeclaration(_)).get
        val x1 = subs(1).map(AssignmentExpression(_)).get
        val x2 = subs(2).map(Statement(_)).get
        ForInOfStatement8(x0, x1, x2, params, span)
    }
  }
}

case class ForInOfStatement0(x3: LeftHandSideExpression, x5: Expression, x7: Statement, parserParams: List[Boolean], span: Span) extends ForInOfStatement {
  x3.parent = Some(this)
  x5.parent = Some(this)
  x7.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x7, d(x5, d(x3, 0)))
  def fullList: List[(String, Value)] = l("Statement", x7, l("Expression", x5, l("LeftHandSideExpression", x3, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"for ( $x3 in $x5 ) $x7"
  }
}

case class ForInOfStatement1(x3: ForBinding, x5: Expression, x7: Statement, parserParams: List[Boolean], span: Span) extends ForInOfStatement {
  x3.parent = Some(this)
  x5.parent = Some(this)
  x7.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x7, d(x5, d(x3, 0)))
  def fullList: List[(String, Value)] = l("Statement", x7, l("Expression", x5, l("ForBinding", x3, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"for ( var $x3 in $x5 ) $x7"
  }
}

case class ForInOfStatement2(x2: ForDeclaration, x4: Expression, x6: Statement, parserParams: List[Boolean], span: Span) extends ForInOfStatement {
  x2.parent = Some(this)
  x4.parent = Some(this)
  x6.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x6, d(x4, d(x2, 0)))
  def fullList: List[(String, Value)] = l("Statement", x6, l("Expression", x4, l("ForDeclaration", x2, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"for ( $x2 in $x4 ) $x6"
  }
}

case class ForInOfStatement3(x3: LeftHandSideExpression, x5: AssignmentExpression, x7: Statement, parserParams: List[Boolean], span: Span) extends ForInOfStatement {
  x3.parent = Some(this)
  x5.parent = Some(this)
  x7.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x7, d(x5, d(x3, 0)))
  def fullList: List[(String, Value)] = l("Statement", x7, l("AssignmentExpression", x5, l("LeftHandSideExpression", x3, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"for ( $x3 of $x5 ) $x7"
  }
}

case class ForInOfStatement4(x3: ForBinding, x5: AssignmentExpression, x7: Statement, parserParams: List[Boolean], span: Span) extends ForInOfStatement {
  x3.parent = Some(this)
  x5.parent = Some(this)
  x7.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x7, d(x5, d(x3, 0)))
  def fullList: List[(String, Value)] = l("Statement", x7, l("AssignmentExpression", x5, l("ForBinding", x3, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"for ( var $x3 of $x5 ) $x7"
  }
}

case class ForInOfStatement5(x2: ForDeclaration, x4: AssignmentExpression, x6: Statement, parserParams: List[Boolean], span: Span) extends ForInOfStatement {
  x2.parent = Some(this)
  x4.parent = Some(this)
  x6.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x6, d(x4, d(x2, 0)))
  def fullList: List[(String, Value)] = l("Statement", x6, l("AssignmentExpression", x4, l("ForDeclaration", x2, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"for ( $x2 of $x4 ) $x6"
  }
}

case class ForInOfStatement6(x4: LeftHandSideExpression, x6: AssignmentExpression, x8: Statement, parserParams: List[Boolean], span: Span) extends ForInOfStatement {
  x4.parent = Some(this)
  x6.parent = Some(this)
  x8.parent = Some(this)
  def idx: Int = 6
  def k: Int = d(x8, d(x6, d(x4, 0)))
  def fullList: List[(String, Value)] = l("Statement", x8, l("AssignmentExpression", x6, l("LeftHandSideExpression", x4, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"for await ( $x4 of $x6 ) $x8"
  }
}

case class ForInOfStatement7(x4: ForBinding, x6: AssignmentExpression, x8: Statement, parserParams: List[Boolean], span: Span) extends ForInOfStatement {
  x4.parent = Some(this)
  x6.parent = Some(this)
  x8.parent = Some(this)
  def idx: Int = 7
  def k: Int = d(x8, d(x6, d(x4, 0)))
  def fullList: List[(String, Value)] = l("Statement", x8, l("AssignmentExpression", x6, l("ForBinding", x4, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"for await ( var $x4 of $x6 ) $x8"
  }
}

case class ForInOfStatement8(x3: ForDeclaration, x5: AssignmentExpression, x7: Statement, parserParams: List[Boolean], span: Span) extends ForInOfStatement {
  x3.parent = Some(this)
  x5.parent = Some(this)
  x7.parent = Some(this)
  def idx: Int = 8
  def k: Int = d(x7, d(x5, d(x3, 0)))
  def fullList: List[(String, Value)] = l("Statement", x7, l("AssignmentExpression", x5, l("ForDeclaration", x3, Nil))).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"for await ( $x3 of $x5 ) $x7"
  }
}
