package kr.ac.kaist.ase.parser

import kr.ac.kaist.ase.util.Useful.cached
import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.input.{ Reader, Position }

trait Lexer extends RegexParsers with UnicodeRegex {
  // not skip white spaces
  override def skipWhitespace = false

  // lexer type
  type Lexer = Parser[String]
  type FLexer = Parser[String => String]
  implicit def str2lexer(str: String): Lexer = literal(str)
  implicit def regex2lexer(r: Regex): Lexer = regex(r)

  // basic lexers
  lazy val WhiteSpace: Lexer = TAB | VT | FF | SP | NBSP | ZWNBSP | USP
  lazy val LineTerminator: Lexer = LF | CR | LS | PS
  lazy val LineTerminatorSequence: Lexer = LF | CR <~ -LF | LS | PS | s(CR, LF)

  // empty
  val empty: Lexer = success("")

  // optional
  def opt(parser: Lexer): Lexer = parser | empty

  // lookahead syntax
  implicit def lookaheadSyntax[L <% Lexer](parser: L): LookaheadSyntax = new LookaheadSyntax(parser)
  class LookaheadSyntax(parser: Lexer) {
    def unary_-(): Lexer = Parser { in =>
      parser(in) match {
        case Success(_, _) => Failure("Wrong Lookahead", in)
        case _ => Success("", in)
      }
    }
    def unary_+(): Lexer = Parser { in =>
      parser(in) match {
        case s @ Success(_, _) => Success("", in)
        case _ => Failure("Wrong Lookahead", in)
      }
    }
  }

  // butnot syntax
  private val butnotCache = cached[(Lexer, Lexer), Lexer] {
    case (parser, cond) => parser.filter(s => parseAll(cond, s).isEmpty)
  }
  implicit def butnotSyntax(parser: Lexer): ButnotSyntax = new ButnotSyntax(parser)
  class ButnotSyntax(parser: Lexer) {
    def \(cond: Lexer): Lexer = butnotCache((parser, cond))
  }

  // sequence
  def s(p1: => Lexer): Lexer = p1
  def s(p1: => Lexer, p2: => Lexer): Lexer =
    p1 ~ p2 ^^ { case x1 ~ x2 => x1 + x2 }
  def s(p1: => Lexer, p2: => Lexer, p3: => Lexer): Lexer =
    p1 ~ p2 ~ p3 ^^ { case x1 ~ x2 ~ x3 => x1 + x2 + x3 }
  def s(p1: => Lexer, p2: => Lexer, p3: => Lexer, p4: => Lexer): Lexer =
    p1 ~ p2 ~ p3 ~ p4 ^^ { case x1 ~ x2 ~ x3 ~ x4 => x1 + x2 + x3 + x4 }
  def s(p1: => Lexer, p2: => Lexer, p3: => Lexer, p4: => Lexer, p5: => Lexer): Lexer =
    p1 ~ p2 ~ p3 ~ p4 ~ p5 ^^ { case x1 ~ x2 ~ x3 ~ x4 ~ x5 => x1 + x2 + x3 + x4 + x5 }
  def s(p1: => Lexer, p2: => Lexer, p3: => Lexer, p4: => Lexer, p5: => Lexer, p6: => Lexer): Lexer =
    p1 ~ p2 ~ p3 ~ p4 ~ p5 ~ p6 ^^ { case x1 ~ x2 ~ x3 ~ x4 ~ x5 ~ x6 => x1 + x2 + x3 + x4 + x5 + x6 }

  def sLL(p1: => Lexer): FLexer = p1 ^^ { case x1 => (x0: String) => x0 + x1 }

  // sequence with skips
  def ss(seq: Lexer*): Lexer = ss(seq.toList)
  def ss(list: List[Lexer]): Lexer = if (list.length == 0) empty else list.map(x => {
    if (x eq strNoLineTerminator) x
    else Skip ~> x
  }).reduce(_ ~ _ ^^^ "")

  // skip
  lazy val Skip: Lexer = ((WhiteSpace | LineTerminator | Comment)*) ^^ { _.mkString }

  // no LineTerminator lexer
  lazy val strNoLineTerminator: Lexer = +Skip.filter(s => lines.findFirstIn(s).isEmpty)

  // resolve left recursions
  def resolveLL(f: Lexer, s: FLexer): Lexer = {
    lazy val p: FLexer = s ~ p ^^ { case b ~ f => (x: String) => f(b(x)) } | success((x: String) => x)
    f ~ p ^^ { case a ~ f => f(a) }
  }
}
