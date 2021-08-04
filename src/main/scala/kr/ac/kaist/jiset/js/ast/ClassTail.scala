package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ClassTail extends AST { val kind: String = "ClassTail" }

object ClassTail {
  def apply(data: Json): ClassTail = AST(data) match {
    case Some(compressed) => ClassTail(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ClassTail = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ClassHeritage(_))
        val x1 = subs(1).map(ClassBody(_))
        ClassTail0(x0, x1, params, span)
    }
  }
}

case class ClassTail0(x0: Option[ClassHeritage], x2: Option[ClassBody], parserParams: List[Boolean], span: Span) extends ClassTail {
  x0.foreach((m) => m.parent = Some(this))
  x2.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x2, d(x0, 0))
  def fullList: List[(String, PureValue)] = l("Option[ClassBody]", x2, l("Option[ClassHeritage]", x0, Nil)).reverse
  def maxK: Int = 3
  override def toString: String = {
    s"${x0.getOrElse("")} { ${x2.getOrElse("")} }"
  }
}
