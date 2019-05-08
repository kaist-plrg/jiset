package kr.ac.kaist.ase

import scala.util.parsing.input._
import scala.util.parsing.combinator._
import scala.language.implicitConversions

trait TokenParsers extends Parsers {
  type Elem = Char

  implicit def parser2token[T](p: => super.Parser[T]): TokenParser[T] = {
    lazy val q = p
    new TokenParser[T] {
      def apply(in: Input) = q(in)
    }
  }

  implicit def text(s: String): TokenParser[String] = new TokenParser[String] {
    def apply(in: Input) = {
      val source = in.source
      val offset = in.offset
      var start = offset
      while (start < source.length && source.charAt(start).isWhitespace) start += 1
      var i = 0
      var j = start
      while (i < s.length && j < source.length && s.charAt(i) == source.charAt(j)) {
        i += 1
        j += 1
      }
      if (i == s.length)
        Success(source.subSequence(start, j).toString, in.drop(j - offset))
      else {
        val found = if (start == source.length()) "end of source" else "'" + source.charAt(start) + "'"
        Failure("'" + s + "' expected but " + found + " found", in.drop(start - offset))
      }
    }
  }

  def tag(s: String): TokenParser[String] = new TokenParser[String] {
    def apply(in: Input) = {
      text(s + ":")(in) match {
        case Success(_, in) =>
          val source = in.source
          val start = in.offset
          var j = start
          while (j < source.length && !source.charAt(j).isWhitespace) j += 1
          Success(source.subSequence(start, j).toString, in.drop(j - start))
        case f => f
      }
    }
  }

  lazy val id = tag("id")
  lazy val value = tag("value")
  lazy val code = tag("code")
  lazy val const = tag("const")
  lazy val linelist = text("line-list")

  abstract class TokenParser[+T] extends super.Parser[T] {
    def |[U >: T](q0: => TokenParser[U]): TokenParser[U] = new TokenParser[U] {
      lazy val q = q0 // lazy argument
      def apply(in: Input) = {
        val res1 = TokenParser.this(in)
        val res2 = q(in)

        (res1, res2) match {
          case (s1 @ Success(t1, _), s2 @ Success(t2, _)) => Error(s"Both $t1 and $t2 succeeded", in)
          case _ => res1 append res2
        }
      }
      override def toString = "|"
    }
  }

  def phrase[T](p: TokenParser[T]) = new TokenParser[T] {
    def apply(in: Input) = p(in) match {
      case s @ Success(out, in1) =>
        if (in1.atEnd) s
        else Failure("end of input expected", in1)
      case ns => ns
    }
  }

  /** Parse some prefix of reader `in` with parser `p`. */
  def parse[T](p: TokenParser[T], in: Reader[Char]): ParseResult[T] =
    p(in)

  /** Parse some prefix of character sequence `in` with parser `p`. */
  def parse[T](p: TokenParser[T], in: java.lang.CharSequence): ParseResult[T] =
    p(new CharSequenceReader(in))

  /** Parse some prefix of reader `in` with parser `p`. */
  def parse[T](p: TokenParser[T], in: java.io.Reader): ParseResult[T] =
    p(new PagedSeqReader(PagedSeq.fromReader(in)))

  /** Parse all of reader `in` with parser `p`. */
  def parseAll[T](p: TokenParser[T], in: Reader[Char]): ParseResult[T] =
    parse(phrase(p), in)

  /** Parse all of reader `in` with parser `p`. */
  def parseAll[T](p: TokenParser[T], in: java.io.Reader): ParseResult[T] =
    parse(phrase(p), in)

  /** Parse all of character sequence `in` with parser `p`. */
  def parseAll[T](p: TokenParser[T], in: java.lang.CharSequence): ParseResult[T] =
    parse(phrase(p), in)
}

object Parser extends TokenParsers
