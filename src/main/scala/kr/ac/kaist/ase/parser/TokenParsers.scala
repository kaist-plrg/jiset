package kr.ac.kaist.ase.parser

import kr.ac.kaist.ase.LINE_SEP
import kr.ac.kaist.ase.algorithm._
import scala.util.parsing.combinator._
import scala.util.parsing.input._

trait TokenParsers extends Parsers {
  type Elem = Token
  case class TokenReader(tokens: List[Token]) extends Reader[Token] {
    def first: Token = tokens.head
    def rest: TokenReader = TokenReader(tokens.tail)
    def pos: Position = NoPosition
    def atEnd: Boolean = tokens.isEmpty

    override def toString: String = Token.getString(tokens)
  }

  private def firstMap[T](in: Input, f: Token => ParseResult[T]): ParseResult[T] = {
    if (in.atEnd) Failure("no more tokens", in)
    else f(in.first)
  }

  private val wordChars = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') :+ '_').toSet
  private val numChars = ('0' to '9').toSet

  private def splitText(s: String): List[String] = {
    var list = List[String]()
    var prevWordChar = false
    for (ch <- s) {
      val isWordChar = wordChars contains ch
      if (prevWordChar && isWordChar) list = (list.head + ch) :: list.tail
      else if (!ch.isSpaceChar) list ::= ch.toString
      prevWordChar = isWordChar
    }
    list.reverse
  }

  implicit def literal(s: String): Parser[List[String]] = Parser(in => {
    val init = success[List[String]](Nil)(in)
    val texts = splitText(s)
    ((init /: texts) {
      case (Success(res, in), x) => firstMap(in, _ match {
        case Text(y) if x.toLowerCase == y.toLowerCase => Success(y :: res, in.rest)
        case t => Failure(s"`Text($x)` expected but `$t` found", in)
      })
      case (e, _) => e
    }).map(_.reverse)
  })

  def const: Parser[String] = Parser(in => firstMap(in, _ match {
    case Const(x) => Success(x, in.rest)
    case t => Failure(s"`Const(_)` expected but `$t` found", in)
  }))

  def value: Parser[String] = Parser(in => firstMap(in, _ match {
    case Value(x) => Success(x, in.rest)
    case t => Failure(s"`Value(_)` expected but `$t` found", in)
  }))

  def text: Parser[String] = Parser(in => firstMap(in, _ match {
    case Text(x) => Success(x, in.rest)
    case t => Failure(s"`Text(_)` expected but `$t` found", in)
  }))

  def id: Parser[String] = Parser(in => firstMap(in, _ match {
    case Id(x) => Success(x, in.rest)
    case t => Failure(s"`Id(_)` expected but `$t` found", in)
  }))

  def next: Parser[Int] = Parser(in => firstMap(in, _ match {
    case Next(k) => Success(k, in.rest)
    case t => Failure(s"`Next(_)` expected but `$t` found", in)
  }))

  def in: Parser[String] = Parser(in => firstMap(in, _ match {
    case In => Success("", in.rest)
    case t => Failure(s"`In` expected but `$t` found", in)
  }))

  def out: Parser[String] = Parser(in => firstMap(in, _ match {
    case Out => Success("", in.rest)
    case t => Failure(s"`Out` expected but `$t` found", in)
  }))

  def all: Parser[Token] = Parser(in => firstMap(in, token => Success(token, in.rest)))

  def end: Parser[String] = Parser(in => {
    if (in.atEnd) Success("", in)
    else Failure("end of input expected", in)
  })

  def word: Parser[String] = Parser(in => text(in).mapPartial(_ match {
    case s if wordChars contains s.head => s
  }, s => s"`$s` is not word"))

  def number: Parser[String] = Parser(in => text(in).mapPartial(_ match {
    case s if numChars contains s.head => s
  }, s => s"`$s` is not number"))

  def rest: Parser[List[String]] = rep(token)
  def step: Parser[(List[String], Int)] = rest ~ next ^^ { case s ~ k => (s, k) }
  def token: Parser[String] = const | value | text | id | stepList
  def stepList: Parser[String] = in ~> rep1(step) <~ out ^^^ "step-list"

  override def phrase[T](p: Parser[T]): Parser[T] =
    super.phrase(p <~ end)

  def parse[T](p: Parser[T], tokenReader: TokenReader): ParseResult[T] =
    p(tokenReader)

  def parse[T](p: Parser[T], tokens: List[Token]): ParseResult[T] =
    parse(p, TokenReader(tokens))

  def parseAll[T](p: Parser[T], tokenReader: TokenReader): ParseResult[T] =
    phrase(p)(tokenReader)

  def parseAll[T](p: Parser[T], tokens: List[Token]): ParseResult[T] =
    parse(phrase(p), tokens)
}
