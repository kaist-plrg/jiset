package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ExportDeclaration extends AST { val kind: String = "ExportDeclaration" }

object AbsExportDeclaration extends ExportDeclaration with AbsAST

object ExportDeclaration {
  def apply(data: Json): ExportDeclaration = AST(data) match {
    case Some(compressed) => ExportDeclaration(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ExportDeclaration = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ExportFromClause(_)).get
        val x1 = subs(1).map(FromClause(_)).get
        ExportDeclaration0(x0, x1, params, span)
      case 1 =>
        val x0 = subs(0).map(NamedExports(_)).get
        ExportDeclaration1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(VariableStatement(_)).get
        ExportDeclaration2(x0, params, span)
      case 3 =>
        val x0 = subs(0).map(Declaration(_)).get
        ExportDeclaration3(x0, params, span)
      case 4 =>
        val x0 = subs(0).map(HoistableDeclaration(_)).get
        ExportDeclaration4(x0, params, span)
      case 5 =>
        val x0 = subs(0).map(ClassDeclaration(_)).get
        ExportDeclaration5(x0, params, span)
      case 6 =>
        val x0 = subs(0).map(AssignmentExpression(_)).get
        ExportDeclaration6(x0, params, span)
    }
  }
}

case class ExportDeclaration0(x1: ExportFromClause, x2: FromClause, parserParams: List[Boolean], span: Span) extends ExportDeclaration {
  x1.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x2, d(x1, 0))
  def fullList: List[(String, PureValue)] = l("FromClause", x2, l("ExportFromClause", x1, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"export $x1 $x2 ;"
  }
}

case class ExportDeclaration1(x1: NamedExports, parserParams: List[Boolean], span: Span) extends ExportDeclaration {
  x1.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("NamedExports", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"export $x1 ;"
  }
}

case class ExportDeclaration2(x1: VariableStatement, parserParams: List[Boolean], span: Span) extends ExportDeclaration {
  x1.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("VariableStatement", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"export $x1"
  }
}

case class ExportDeclaration3(x1: Declaration, parserParams: List[Boolean], span: Span) extends ExportDeclaration {
  x1.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("Declaration", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"export $x1"
  }
}

case class ExportDeclaration4(x2: HoistableDeclaration, parserParams: List[Boolean], span: Span) extends ExportDeclaration {
  x2.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x2, 0)
  def fullList: List[(String, PureValue)] = l("HoistableDeclaration", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"export default $x2"
  }
}

case class ExportDeclaration5(x2: ClassDeclaration, parserParams: List[Boolean], span: Span) extends ExportDeclaration {
  x2.parent = Some(this)
  def idx: Int = 5
  def k: Int = d(x2, 0)
  def fullList: List[(String, PureValue)] = l("ClassDeclaration", x2, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"export default $x2"
  }
}

case class ExportDeclaration6(x3: AssignmentExpression, parserParams: List[Boolean], span: Span) extends ExportDeclaration {
  x3.parent = Some(this)
  def idx: Int = 6
  def k: Int = d(x3, 0)
  def fullList: List[(String, PureValue)] = l("AssignmentExpression", x3, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"export default $x3 ;"
  }
}
