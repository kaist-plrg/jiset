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
