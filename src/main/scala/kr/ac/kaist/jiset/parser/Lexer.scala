package kr.ac.kaist.jiset.parser

import kr.ac.kaist.jiset.util.Useful.cached
import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input._

trait Lexer extends RegexParsers with EPackratParsers with UnicodeRegex {
  // not skip white spaces
  override def skipWhitespace = false

  // lexer type
  type Lexer = EPackratParser[String]

  // implicit conversion to Lexer
  implicit def str2lexer[T](str: String): Lexer = parser2packrat(literal(str))
  implicit def regex2lexer[T](r: Regex): Lexer = parser2packrat(regex(r))

  // implicit lexer helper
  implicit class LexerHelper[T](val parser: T)(implicit f: T => Parser[String]) {
    // lookahead symbols
    def unary_-(): Parser[String] = "" <~ not(parser)
    def unary_+(): Parser[String] = "" <~ guard(parser)

    // sequence
    def %(that: => Parser[String]): Parser[String] =
      this.parser ~ that.parser ^^ { case x ~ y => x + y }

    // sequence with Skip
    def %%(that: => Parser[String]): Parser[String] = %(
      if (that.parser eq strNoLineTerminator) that.parser
      else Skip ~> that.parser
    )

    // exclusion (butnot) symbol
    def \(cond: => Parser[String]): Parser[String] =
      this.parser.filter(s => parseAll(cond.parser, s).isEmpty)

    // optional symbol
    def opt(): Parser[String] = parser | empty
  }

  // basic lexers
  lazy val WhiteSpace: Lexer = TAB | VT | FF | SP | NBSP | ZWNBSP | USP
  lazy val LineTerminator: Lexer = LF | CR | LS | PS
  lazy val LineTerminatorSequence: Lexer = LF | CR <~ -LF | LS | PS | CR % LF

  // empty
  val empty: Lexer = ""

  // skip
  lazy val Skip: Lexer =
    ((WhiteSpace | LineTerminator | Comment)*) ^^ { _.mkString }

  // no LineTerminator lexer
  lazy val strNoLineTerminator: Lexer =
    +Skip.filter(s => lines.findFirstIn(s).isEmpty)

  // Parse charater reader `in` with parser `p`
  def parse(p: Lexer, in: Reader[Char]): ParseResult[String] = {
    p(new EPackratReader(in))
  }

  // Parse character sequence `in` with parser `p`
  def parse(p: Lexer, in: java.lang.CharSequence): ParseResult[String] =
    parse(p, new CharSequenceReader(in))

  // Parse reader `in` with parser `p`
  def parse(p: Lexer, in: java.io.Reader): ParseResult[String] =
    parse(p, new PagedSeqReader(PagedSeq.fromReader(in)))
}
