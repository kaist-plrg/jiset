package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait Declaration extends AST { val kind: String = "Declaration" }

object AbsDeclaration extends Declaration with AbsAST

object Declaration {
  def apply(data: Json): Declaration = AST(data) match {
    case Some(compressed) => Declaration(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): Declaration = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(HoistableDeclaration(_)).get
        Declaration0(x0, params, span)
      case 1 =>
        val x0 = subs(0).map(ClassDeclaration(_)).get
        Declaration1(x0, params, span)
      case 2 =>
        val x0 = subs(0).map(LexicalDeclaration(_)).get
        Declaration2(x0, params, span)
    }
  }
}

case class Declaration0(x0: HoistableDeclaration, parserParams: List[Boolean], span: Span) extends Declaration {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("HoistableDeclaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Declaration1(x0: ClassDeclaration, parserParams: List[Boolean], span: Span) extends Declaration {
  x0.parent = Some(this)
  def idx: Int = 1
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ClassDeclaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}

case class Declaration2(x0: LexicalDeclaration, parserParams: List[Boolean], span: Span) extends Declaration {
  x0.parent = Some(this)
  def idx: Int = 2
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("LexicalDeclaration", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
