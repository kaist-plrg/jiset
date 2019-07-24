package kr.ac.kaist.ase.parser

import scala.util.matching.Regex
import scala.util.parsing.combinator._

trait Lexer extends RegexParsers with PackratParsers with UnicodeRegex {
  // not skip white spaces
  override def skipWhitespace = false

  // lexer type
  type Lexer = PackratParser[String]
  implicit def str2lexer(str: String): Lexer = literal(str)
  implicit def regex2lexer(r: Regex): Lexer = regex(r)

  // basic lexers
  lazy val WhiteSpace: Lexer = TAB | VT | FF | SP | NBSP | ZWNBSP | USP
  lazy val LineTerminator: Lexer = LF | CR | LS | PS
  lazy val LineTerminatorSequence: Lexer = LF | CR <~ -LF | LS | PS | seq(CR, LF)

  // optional
  def opt(parser: => Lexer): Lexer = parser | ""

  // lookahead syntax
  implicit def lookaheadSyntax[T, A <% Parser[T]](parser: => A): LookaheadSyntax[T] = new LookaheadSyntax(parser)
  class LookaheadSyntax[T](parser: => Parser[T]) {
    def unary_-(): Parser[Unit] = Parser { in =>
      parser(in) match {
        case Success(_, _) => Failure("Wrong Lookahead", in)
        case _ => Success((), in)
      }
    }
    def unary_+(): Parser[Unit] = Parser { in =>
      parser(in) match {
        case s @ Success(_, _) => Success((), in)
        case _ => Failure("Wrong Lookahead", in)
      }
    }
  }

  // butnot syntax
  implicit def butnotSyntax[A <% Parser[String]](parser: => A): ButnotSyntax = new ButnotSyntax(parser)
  class ButnotSyntax(parser: => Parser[String]) {
    def \(cond: => Parser[String]): Parser[String] = parser match {
      case (parser: Parser[String]) => parser.filter(s => parseAll(cond, s).isEmpty)
    }
  }

  // sequence
  def seq(p1: => Lexer): Lexer = p1
  def seq(p1: => Lexer, p2: => Lexer): Lexer =
    p1 ~ p2 ^^ { case x1 ~ x2 => x1 + x2 }
  def seq(p1: => Lexer, p2: => Lexer, p3: => Lexer): Lexer =
    p1 ~ p2 ~ p3 ^^ { case x1 ~ x2 ~ x3 => x1 + x2 + x3 }
  def seq(p1: => Lexer, p2: => Lexer, p3: => Lexer, p4: => Lexer): Lexer =
    p1 ~ p2 ~ p3 ~ p4 ^^ { case x1 ~ x2 ~ x3 ~ x4 => x1 + x2 + x3 + x4 }
  def seq(p1: => Lexer, p2: => Lexer, p3: => Lexer, p4: => Lexer, p5: => Lexer): Lexer =
    p1 ~ p2 ~ p3 ~ p4 ~ p5 ^^ { case x1 ~ x2 ~ x3 ~ x4 ~ x5 => x1 + x2 + x3 + x4 + x5 }
  def seq(p1: => Lexer, p2: => Lexer, p3: => Lexer, p4: => Lexer, p5: => Lexer, p6: => Lexer): Lexer =
    p1 ~ p2 ~ p3 ~ p4 ~ p5 ~ p6 ^^ { case x1 ~ x2 ~ x3 ~ x4 ~ x5 ~ x6 => x1 + x2 + x3 + x4 + x5 + x6 }

  // skip
  lazy val Skip: Lexer = ((WhiteSpace | LineTerminator | Comment)*) ^^ { _.mkString }

  // no LineTerminator lexer
  lazy val strNoLineTerminator: Lexer = "" <~ +(Skip.filter(s => lines.findFirstIn(s).isEmpty))
}
