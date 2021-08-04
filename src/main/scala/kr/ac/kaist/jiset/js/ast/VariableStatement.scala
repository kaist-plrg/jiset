package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait VariableStatement extends AST { val kind: String = "VariableStatement" }

object VariableStatement {
  def apply(data: Json): VariableStatement = AST(data) match {
    case Some(compressed) => VariableStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): VariableStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(VariableDeclarationList(_)).get
        VariableStatement0(x0, params, span)
    }
  }
}

case class VariableStatement0(x1: VariableDeclarationList, parserParams: List[Boolean], span: Span) extends VariableStatement {
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, 0)
  def fullList: List[(String, PureValue)] = l("VariableDeclarationList", x1, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"var $x1 ;"
  }
}
