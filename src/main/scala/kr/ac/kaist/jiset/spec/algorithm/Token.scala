package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.jiset.LINE_SEP

// tokens
trait Token {
  def is(str: String): Boolean = toString == str
}
object Token {
  def getString(tokens: List[Token]): String = {
    val sb = new StringBuilder
    val TAB = 2
    var indent = 0
    def newline: Unit = sb.append(LINE_SEP).append(" " * indent)
    def t(token: Token): Unit = token match {
      case (_: NormalToken) | (_: StepList) => sb.append(token).append(" ")
      case Next(_) => newline
      case Out =>
      case In =>
        indent += TAB; newline
    }
    def ts(tokens: List[Token]): Unit = tokens match {
      case Out :: Next(_) :: rest =>
        indent -= TAB; ts(rest)
      case v :: rest =>
        t(v); ts(rest)
      case Nil =>
    }
    ts(tokens)
    sb.toString
  }
}

// normal tokens
abstract class NormalToken(name: String, content: String) extends Token {
  override def toString: String = this match {
    case Text(t) => t
    case _ => s"$name:{$content}"
  }
  def getContent: String = content
  override def is(str: String): Boolean = toString == str || name + ":" == str
}
case class Const(const: String) extends NormalToken("const", const)
case class Code(code: String) extends NormalToken("code", code)
case class Value(value: String) extends NormalToken("value", value)
case class Id(id: String) extends NormalToken("id", id)
case class Text(text: String) extends NormalToken("text", text)
case class Star(text: String) extends NormalToken("star", text)
case class Nt(nt: String) extends NormalToken("nt", nt)
case class Sup(sup: Step) extends NormalToken("sup", Token.getString(sup.tokens))
case class Link(link: Option[String]) extends NormalToken("link", link.getOrElse(""))
case class Gr(grammar: String, subs: List[String]) extends NormalToken("grammar", grammar)

// step lists
case class StepList(steps: List[Step]) extends Token {
  override def toString: String = "step-list:{...}"
}

// special tokens only in steps
case class Next(k: Int) extends Token
case object In extends Token
case object Out extends Token
