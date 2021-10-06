package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ForDeclaration extends AST { val kind: String = "ForDeclaration" }

object AbsForDeclaration extends ForDeclaration with AbsAST

object ForDeclaration {
  def apply(data: Json): ForDeclaration = AST(data) match {
    case Some(compressed) => ForDeclaration(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ForDeclaration = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(LetOrConst(_)).get
        val x1 = subs(1).map(ForBinding(_)).get
        ForDeclaration0(x0, x1, params, span)
    }
  }
}

case class ForDeclaration0(x0: LetOrConst, x1: ForBinding, parserParams: List[Boolean], span: Span) extends ForDeclaration {
  x0.parent = Some(this)
  x1.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x1, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("ForBinding", x1, l("LetOrConst", x0, Nil)).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0 $x1"
  }
}
