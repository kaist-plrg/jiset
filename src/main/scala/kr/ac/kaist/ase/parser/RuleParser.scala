package kr.ac.kaist.ase.parser

import scala.util.parsing.combinator._

case class Rule(name: String, tokens: List[Token])
trait Token
case class NonTermainl(ty: RuleType, str: String) extends Token
case class Terminal(str: String) extends Token

sealed abstract class RuleType(str: String) {
  override def toString: String = str
}
case class ListType(ty: RuleType) extends RuleType(s"List[$ty]")
case class OptionType(ty: RuleType) extends RuleType(s"Option[$ty]")
case class ClassType(f: String) extends RuleType(f)

object RuleParser extends RegexParsers with ParseTo[Rule] {
  def apply(str: String): Rule = parseAll(rule, str).get
  override def skipWhitespace = false
  lazy val skip: Parser[Unit] = rep(whiteSpace) ^^^ {}
  lazy val spaces: Parser[Unit] = "[ ]+".r ^^^ {}
  lazy val token: Parser[Token] =
    nt | rep1(t) ^^ { ts => Terminal(" <~ " + ts.mkString(" ")) }
  lazy val nt: Parser[NonTermainl] =
    nt0 <~ "?" ^^ { case NonTermainl(ty, s) => NonTermainl(OptionType(ty), s + "?") } |
      (nt0 <~ "*(") ~ (t <~ ")") ^^ { case NonTermainl(ty, s) ~ y => NonTermainl(ListType(ty), s"repsep($s, $y)") } |
      nt0 <~ "*" ^^ { case NonTermainl(ty, s) => NonTermainl(ListType(ty), s"rep($s)") } |
      (nt0 <~ "+(") ~ (t <~ ")") ^^ { case NonTermainl(ty, s) ~ y => NonTermainl(ListType(ty), s"rep1sep($s, $y)") } |
      nt0 <~ "+" ^^ { case NonTermainl(ty, s) => NonTermainl(ListType(ty), s"rep1($s)") } |
      nt0
  lazy val tokens: Parser[List[Token]] = repsep(token, spaces)

  lazy val nt0: Parser[NonTermainl] =
    """[A-Z]\w*(?!\w)""".r ^^ { x => NonTermainl(ClassType(x), x.head.toLower + x.tail) }

  lazy val t: Parser[String] =
    """[a-z]\w*(?!\w)""".r ^^ { x => s""""$x"""" } |
      """\\.""".r ^^ { x => s""""${x.tail}"""" } |
      """[^\w\s]""".r ^^ { x => x }

  lazy val rule: Parser[Rule] =
    (skip ~> nt <~ skip <~ "=" <~ skip) ~ tokens <~ skip ^^ {
      case NonTermainl(ClassType(n), _) ~ y => Rule(n, y)
    }
}
