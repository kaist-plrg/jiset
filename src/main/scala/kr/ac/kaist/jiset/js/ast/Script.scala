package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait Script extends AST { val kind: String = "Script" }

object AbsScript extends Script with AbsAST

object Script {
  def apply(data: Json): Script = AST(data) match {
    case Some(compressed) => Script(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): Script = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(ScriptBody(_))
        Script0(x0, params, span)
    }
  }
}

case class Script0(x0: Option[ScriptBody], parserParams: List[Boolean], span: Span) extends Script {
  x0.foreach((m) => m.parent = Some(this))
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("Option[ScriptBody]", x0, Nil).reverse
  def maxK: Int = 1
  override def toString: String = {
    s"${x0.getOrElse("")}"
  }
}
