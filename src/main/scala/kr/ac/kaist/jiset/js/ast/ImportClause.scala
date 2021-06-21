package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import spray.json._

trait ImportClause extends AST { val kind: String = "ImportClause" }

case class ImportClause0(x0: ImportedDefaultBinding, parserParams: List[Boolean], span: Span) extends ImportClause {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("ImportedDefaultBinding", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ImportClause1(x0: NameSpaceImport, parserParams: List[Boolean], span: Span) extends ImportClause {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("NameSpaceImport", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ImportClause2(x0: NamedImports, parserParams: List[Boolean], span: Span) extends ImportClause {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("NamedImports", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class ImportClause3(x0: ImportedDefaultBinding, x2: NameSpaceImport, parserParams: List[Boolean], span: Span) extends ImportClause {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 3
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("NameSpaceImport", x2, l("ImportedDefaultBinding", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}

case class ImportClause4(x0: ImportedDefaultBinding, x2: NamedImports, parserParams: List[Boolean], span: Span) extends ImportClause {
  x0.parent = Some(this)
  x2.parent = Some(this)
  def idx: Int = 4
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, Value)] = l("NamedImports", x2, l("ImportedDefaultBinding", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 , $x2"
  }
}
