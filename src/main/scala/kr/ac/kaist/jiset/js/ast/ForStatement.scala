package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span

trait ForStatement extends AST { val kind: String = "ForStatement" }

case class ForStatement0(x3: Option[Expression], x5: Option[Expression], x7: Option[Expression], x9: Statement, parserParams: List[Boolean], span: Span) extends ForStatement {
  x3.foreach((m) => m.parent = Some(this))
  x5.foreach((m) => m.parent = Some(this))
  x7.foreach((m) => m.parent = Some(this))
  x9.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x9, d(x7, d(x5, d(x3, 0))))
  def fullList: List[(String, Value)] = l("Statement", x9, l("Option[Expression]2", x7, l("Option[Expression]1", x5, l("Option[Expression]0", x3, Nil)))).reverse
  def maxK: Int = 7
  override def toString: String = {
    s"for ( ${x3.getOrElse("")} ; ${x5.getOrElse("")} ; ${x7.getOrElse("")} ) $x9"
  }
}

case class ForStatement1(x3: VariableDeclarationList, x5: Option[Expression], x7: Option[Expression], x9: Statement, parserParams: List[Boolean], span: Span) extends ForStatement {
  x3.parent = Some(this)
  x5.foreach((m) => m.parent = Some(this))
  x7.foreach((m) => m.parent = Some(this))
  x9.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x9, d(x7, d(x5, d(x3, 0))))
  def fullList: List[(String, Value)] = l("Statement", x9, l("Option[Expression]1", x7, l("Option[Expression]0", x5, l("VariableDeclarationList", x3, Nil)))).reverse
  def maxK: Int = 3
  override def toString: String = {
    s"for ( var $x3 ; ${x5.getOrElse("")} ; ${x7.getOrElse("")} ) $x9"
  }
}

case class ForStatement2(x2: LexicalDeclaration, x3: Option[Expression], x5: Option[Expression], x7: Statement, parserParams: List[Boolean], span: Span) extends ForStatement {
  x2.parent = Some(this)
  x3.foreach((m) => m.parent = Some(this))
  x5.foreach((m) => m.parent = Some(this))
  x7.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x7, d(x5, d(x3, d(x2, 0))))
  def fullList: List[(String, Value)] = l("Statement", x7, l("Option[Expression]1", x5, l("Option[Expression]0", x3, l("LexicalDeclaration", x2, Nil)))).reverse
  def maxK: Int = 3
  override def toString: String = {
    s"for ( $x2 ${x3.getOrElse("")} ; ${x5.getOrElse("")} ) $x7"
  }
}
