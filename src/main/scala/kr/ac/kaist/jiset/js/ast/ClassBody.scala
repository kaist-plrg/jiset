package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ClassBody extends AST { val kind: String = "ClassBody" }

object AbsClassBody extends ClassBody with AbsAST

object ClassBody {
  def apply(data: Json): ClassBody = AST(data) match {
    case Some(compressed) => ClassBody(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ClassBody = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ClassElementList(_)).get
        ClassBody0(x0, params, span)
    }
  }
}

case class ClassBody0(x0: ClassElementList, parserParams: List[Boolean], span: Span) extends ClassBody {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("ClassElementList", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
