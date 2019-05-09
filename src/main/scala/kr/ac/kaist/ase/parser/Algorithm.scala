package kr.ac.kaist.ase.parser

import kr.ac.kaist.ase.LINE_SEP
import kr.ac.kaist.ase.util.Appendable

// algorithms
case class Algorithm(steps: List[Step])
trait AlgorithmParsers[T] extends StepParsers[T] {
  lazy val algorithm: Parser[Algorithm] = stepList ^^ { case sl => Algorithm(sl.steps) }
}
object Algorithm extends AlgorithmParsers[Algorithm] { lazy val rule = algorithm }

// steps
trait Step extends Appendable {
  val tokens: List[Token]
  def getSteps(init: List[Step]): List[Step] = ((this :: init) /: tokens) {
    case (list, StepList(steps)) => (list /: steps) {
      case (list, s) => s.getSteps(list)
    }
    case (list, _) => list
  }
  def getSteps: List[Step] = getSteps(Nil).reverse
  def appendTo(sb: StringBuilder, pre: String): StringBuilder = {
    sb.append(pre).append("<step>")
    tokens.foreach(_.appendTo(sb, pre).append(" "))
    sb.append(pre).append("</step>").append(LINE_SEP)
  }
  def toBriefString: String = {
    val sb = new StringBuilder
    tokens.foreach(_ match {
      case StepList(_) => sb.append("<step-list>...</step-list>")
      case token => token.appendTo(sb, "").append(" ")
    })
    sb.toString
  }
}
case class RawStep(tokens: List[Token]) extends Step
trait StepParsers[T] extends ASEParsers[T] {
  def tagged[T](tag: String, parser: => Parser[T]): Parser[T] = s"<$tag>" ~> parser <~ s"</$tag>"
  def tagged[T](tag: String)(constructor: String => T): Parser[T] = tagged(tag, "[^\\s<]+".r ^^ { constructor(_) })
  lazy val token: Parser[Token] = expr | value | id | stepList | text
  lazy val rawStep: Parser[RawStep] = tagged("step", rep1(token) ^^ { RawStep(_) })

  // steps
  lazy val step: Parser[Step] = rawStep

  // expressions
  lazy val expr: Parser[Expr] = failure("not expression")

  // basic tokens
  lazy val value: Parser[Value] = tagged("value")(Value(_))
  lazy val id: Parser[Id] = tagged("id")(Id(_))
  lazy val stepList: Parser[StepList] = tagged("step-list", rep(rawStep) ^^ { StepList(_) })
  val textRegex = ".*<.*>.*".r
  lazy val text: Parser[Text] = ("[^\\s<]+".r | "[^\\s]+".r.filter(_ match {
    case textRegex() => false
    case _ => true
  })) ^^ { Text(_) }
}

// tokens
trait Token extends Appendable {
  def appendTo(sb: StringBuilder, pre: String): StringBuilder = this match {
    case Value(t) => sb.append("<value>").append(t).append("</value>")
    case Id(t) => sb.append("<id>").append(t).append("</id>")
    case StepList(steps) =>
      sb.append("<step-list>").append(LINE_SEP)
      steps.foreach(_.appendTo(sb, pre + TAB))
      sb.append("</step-list>").append(LINE_SEP)
    case Text(t) => sb.append(t)
  }
}

// expressions
trait Expr extends Token

// basic tokens
case class Value(text: String) extends Token
case class Id(text: String) extends Token
case class StepList(steps: List[RawStep]) extends Token
case class Text(text: String) extends Token

////////////////////////////////////////////////////////////////////////////////
// CFG for Steps and Expressions
////////////////////////////////////////////////////////////////////////////////
