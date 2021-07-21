package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ModuleSpecifier extends AST { val kind: String = "ModuleSpecifier" }

object ModuleSpecifier {
  def apply(data: Json): ModuleSpecifier = AST(data) match {
    case Some(compressed) => ModuleSpecifier(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ModuleSpecifier = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(Lexical(_)).get
        ModuleSpecifier0(x0, params, span)
    }
  }
}

case class ModuleSpecifier0(x0: Lexical, parserParams: List[Boolean], span: Span) extends ModuleSpecifier {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, Value)] = l("Lexical", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
