package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ExportDeclaration extends AST { val kind: String = "ExportDeclaration" }

case class ExportDeclaration0(x1: ExportFromClause, x2: FromClause, parserParams: List[Boolean], span: Span) extends ExportDeclaration {
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, Value)] = l("FromClause", x2, l("ExportFromClause", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"export $x1 $x2 ;"
  }
}

case class ExportDeclaration1(x1: NamedExports, parserParams: List[Boolean], span: Span) extends ExportDeclaration {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("NamedExports", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"export $x1 ;"
  }
}

case class ExportDeclaration2(x1: VariableStatement, parserParams: List[Boolean], span: Span) extends ExportDeclaration {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("VariableStatement", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"export $x1"
  }
}

case class ExportDeclaration3(x1: Declaration, parserParams: List[Boolean], span: Span) extends ExportDeclaration {
  x1.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x1, 0)
  def fullList: List[(String, Value)] = l("Declaration", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"export $x1"
  }
}

case class ExportDeclaration4(x2: HoistableDeclaration, parserParams: List[Boolean], span: Span) extends ExportDeclaration {
  x2.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("HoistableDeclaration", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"export default $x2"
  }
}

case class ExportDeclaration5(x2: ClassDeclaration, parserParams: List[Boolean], span: Span) extends ExportDeclaration {
  x2.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x2, 0)
  def fullList: List[(String, Value)] = l("ClassDeclaration", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"export default $x2"
  }
}

case class ExportDeclaration6(x3: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ExportDeclaration {
  x3.parent = Some(this)
  def idx: Int = 6
  def k: Int = d(x3, 0)
  def fullList: List[(String, Value)] = l("AssignmentExpression", x3, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"export default $x3 ;"
  }
}
