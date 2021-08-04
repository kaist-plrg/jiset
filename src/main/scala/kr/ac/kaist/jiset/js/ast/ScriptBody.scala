package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait ScriptBody extends AST { val kind: String = "ScriptBody" }

object ScriptBody {
  def apply(data: Json): ScriptBody = AST(data) match {
    case Some(compressed) => ScriptBody(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): ScriptBody = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        val x0 = subs(0).map(StatementList(_)).get
        ScriptBody0(x0, params, span)
    }
  }
}

case class ScriptBody0(x0: StatementList, parserParams: List[Boolean], span: Span) extends ScriptBody {
  x0.parent = Some(this)
  def idx: Int = 0
  def k: Int = d(x0, 0)
  def fullList: List[(String, PureValue)] = l("StatementList", x0, Nil).reverse
  def maxK: Int = 0
  override def toString: String = {
    s"$x0"
  }
}
