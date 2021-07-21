package kr.ac.kaist.jiset.js.ast

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.Span
import kr.ac.kaist.jiset.util.Useful._
import io.circe._, io.circe.syntax._

trait DebuggerStatement extends AST { val kind: String = "DebuggerStatement" }

object DebuggerStatement {
  def apply(data: Json): DebuggerStatement = AST(data) match {
    case Some(compressed) => DebuggerStatement(compressed)
    case None => error("invalid AST data: $data")
  }
  def apply(data: AST.Compressed): DebuggerStatement = {
    val AST.NormalCompressed(idx, subs, params, span) = data
    idx match {
      case 0 =>
        DebuggerStatement0(params, span)
    }
  }
}

case class DebuggerStatement0(parserParams: List[Boolean], span: Span) extends DebuggerStatement {
  def idx: Int = 0
  def k: Int = 0
  def fullList: List[(String, Value)] = Nil.reverse
  def maxK: Int = 0
  override def toString: String = {
    s"debugger ;"
  }
}
