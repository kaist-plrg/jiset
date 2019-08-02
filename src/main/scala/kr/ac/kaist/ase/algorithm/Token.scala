package kr.ac.kaist.ase.algorithm

import kr.ac.kaist.ase.LINE_SEP
import kr.ac.kaist.ase.error.UnexpectedToken

// tokens
trait Token
case class Const(const: String) extends Token
case class Value(value: String) extends Token
case class Id(id: String) extends Token
case class StepList(steps: List[Step]) extends Token
case class Text(text: String) extends Token
case class Next(k: Int) extends Token
case object In extends Token
case object Out extends Token

object Token {
  def getString(tokens: List[Token]): String = {
    val sb = new StringBuilder
    var TAB = 2
    var indent = 0
    def newline: Unit = sb.append(LINE_SEP).append(" " * indent)
    def t(token: Token): Unit = token match {
      case Const(const) => sb.append("const:").append(const).append(" ")
      case Value(value) => sb.append("value:").append(value).append(" ")
      case Id(id) => sb.append("id:").append(id).append(" ")
      case Text(text) => sb.append(text).append(" ")
      case Next(_) => newline
      case In =>
        indent += TAB; newline
      case _ => throw UnexpectedToken(token)
    }
    def ts(tokens: List[Token]): Unit = tokens match {
      case Next(_) :: Out :: Next(_) :: rest =>
        indent -= TAB; newline; ts(rest)
      case v :: rest =>
        t(v); ts(rest)
      case Nil =>
    }
    ts(tokens)
    sb.toString
  }
}
