package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait LexicalDeclaration extends AST { val kind: String = "LexicalDeclaration" }

object LexicalDeclaration {
  def apply(data: Json): LexicalDeclaration = AST(data) match {
    case Some(compressed) => LexicalDeclaration(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): LexicalDeclaration = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(LetOrConst(_)).get
        val x1 = subs(1).map(BindingList(_)).get
        LexicalDeclaration0(x0, x1, params, span)
    }
  }
}

case class LexicalDeclaration0(x0: LetOrConst, x1: BindingList, parserParams: List[Boolean], span: Span) extends LexicalDeclaration {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("BindingList", x1, l("LetOrConst", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1 ;"
  }
}
