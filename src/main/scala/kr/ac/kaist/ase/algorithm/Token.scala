package kr.ac.kaist.ase.algorithm

import kr.ac.kaist.ase.LINE_SEP
import kr.ac.kaist.ase.util.Appendable

// tokens
trait Token extends Appendable

// basic tokens
trait BasicToken extends Token {
  override def appendTo(sb: StringBuilder, pre: String): StringBuilder = this match {
    case Value(t) => sb.append("<value>").append(t).append("</value>")
    case Id(t) => sb.append("<id>").append(t).append("</id>")
    case StepList(steps) =>
      sb.append("<step-list>").append(LINE_SEP)
      steps.foreach(_.appendTo(sb, pre + TAB))
      sb.append("</step-list>").append(LINE_SEP)
    case Text(t) => sb.append(t)
  }
}
case class Value(text: String) extends BasicToken
case class Id(text: String) extends BasicToken
case class StepList(steps: List[Step]) extends BasicToken
case class Text(text: String) extends BasicToken

// parser for steps
trait TokenParsers { this: AlgorithmParsers =>
  def tagged[T](tag: String, parser: => Parser[T]): Parser[T] = s"<$tag>" ~> parser <~ s"</$tag>"
  def tagged[T](tag: String)(constructor: String => T): Parser[T] = tagged(tag, "[^\\s<]+".r ^^ { constructor(_) })

  // tokens
  lazy val token: Parser[Token] = value | id | stepList | text

  // basic tokens
  lazy val value: Parser[Value] = tagged("value")(Value(_))
  lazy val id: Parser[Id] = tagged("id")(Id(_))
  lazy val stepList: Parser[StepList] = tagged("step-list", rep(step) ^^ { StepList(_) })
  val textRegex = ".*</step>.*".r
  lazy val text: Parser[Text] = ("[^\\s<]+".r | "[^\\s]+".r.filter(_ match {
    case textRegex() => false
    case _ => true
  })) ^^ { Text(_) }
}
