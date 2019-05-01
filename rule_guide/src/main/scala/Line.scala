package NLPjse

import scala.util.parsing.combinator.RegexParsers
import scala.io.Source

trait Token
case class Text(text: String) extends Token
case class Id(text: String) extends Token
case class Value(text: String) extends Token
case class Code(text: String) extends Token
case class Const(text: String) extends Token
case object LineList extends Token

case class Line(tokens: List[Token])
object Line extends LineParser {
  def apply(str: String): Line = parseAll(line, str) match {
    case Success(result, _) => result
    case failure: NoSuccess => scala.sys.error(failure.msg)
  }
  def fromFile(filename: String): List[Line] = {
    Source.fromFile(filename).getLines.map(apply _).toList
  }
}
trait LineParser extends RegexParsers {
  lazy val str = "\\S+".r
  lazy val text = str ^^ { Text(_) }
  lazy val id = "Id(" ~> str <~ ")" ^^ { Id(_) }
  lazy val value = "Value(" ~> str <~ ")" ^^ { Value(_) }
  lazy val code = "Code(" ~> str <~ ")" ^^ { Code(_) }
  lazy val const = "Const(" ~> str <~ ")" ^^ { Const(_) }
  lazy val token: Parser[Token] = id | value | code | const | text
  lazy val line = rep(token) ^^ { Line(_) }
}
