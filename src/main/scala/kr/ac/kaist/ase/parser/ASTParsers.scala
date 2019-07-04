package kr.ac.kaist.ase.parser

import kr.ac.kaist.ase.model.{ AST, Script }
import kr.ac.kaist.ase.util.Useful._
import scala.collection.mutable
import scala.util.matching.Regex
import scala.util.parsing.combinator._
import scala.util.parsing.input._

trait ASTParsers extends RegexParsers {
  private val self = this

  // not skip white sapces
  override def skipWhitespace = false

  // lookahead
  implicit def lookaheadSyntax[A](parser: => Parser[A]): LookaheadSyntax[A] = new LookaheadSyntax[A](parser)
  class LookaheadSyntax[A](parser: => Parser[A]) {
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

  lazy val STR_MATCH: Parser[String] = ""
  lazy val STR_MISMATCH: Parser[Nothing] = failure("")

  // but not
  implicit def butnotSyntax(parser: => Parser[String]): ButnotSyntax = new ButnotSyntax(parser)
  class ButnotSyntax(parser: => Parser[String]) {
    def \(cond: => Parser[String]): Parser[String] = {
      parser.filter(s => parseAll(cond, s).isEmpty)
    }
  }

  // special characters
  lazy val ZWNJ: Parser[String] = "\u200C"
  lazy val ZWJ: Parser[String] = "\u200D"
  lazy val ZWNBSP: Parser[String] = "\uFEFF"

  lazy val TAB: Parser[String] = "\u0009"
  lazy val VT: Parser[String] = "\u000B"
  lazy val FF: Parser[String] = "\u000C"
  lazy val SP: Parser[String] = "\u0020"
  lazy val NBSP: Parser[String] = "\u00A0"
  lazy val USP: Parser[String] = "[\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000]".r

  lazy val LF: Parser[String] = "\u000A"
  lazy val CR: Parser[String] = "\u000D"
  lazy val LS: Parser[String] = "\u2028"
  lazy val PS: Parser[String] = "\u2029"

  lazy val WhiteSpace: Parser[String] = TAB | VT | FF | SP | NBSP | ZWNBSP | USP
  lazy val LineTerminator: Parser[String] = LF | CR | LS | PS
  lazy val LineTerminatorSequence: Parser[String] = LF | CR <~ -LF | LS | PS | seq(CR, LF)
  lazy val lines: Regex = "[\u000A\u000D\u2028\u2029]".r

  lazy val Unicode: Parser[String] = "(?s).".r
  lazy val IDStart: Parser[String] = UnicodeRegex.IDStart
  lazy val IDContinue: Parser[String] = UnicodeRegex.IDContinue

  lazy val Comment: Parser[String] = """/\*+[^*]*\*+(?:[^/*][^*]*\*+)*/|//[^\u000A\u000D\u2028\u2029]*""".r

  // sequence
  def seq(p1: => Parser[String]): Parser[String] = p1
  def seq(p1: => Parser[String], p2: => Parser[String]): Parser[String] =
    p1 ~ p2 ^^ { case x1 ~ x2 => x1 + x2 }
  def seq(p1: => Parser[String], p2: => Parser[String], p3: => Parser[String]): Parser[String] =
    p1 ~ p2 ~ p3 ^^ { case x1 ~ x2 ~ x3 => x1 + x2 + x3 }
  def seq(p1: => Parser[String], p2: => Parser[String], p3: => Parser[String], p4: => Parser[String]): Parser[String] =
    p1 ~ p2 ~ p3 ~ p4 ^^ { case x1 ~ x2 ~ x3 ~ x4 => x1 + x2 + x3 + x4 }
  def seq(p1: => Parser[String], p2: => Parser[String], p3: => Parser[String], p4: => Parser[String], p5: => Parser[String]): Parser[String] =
    p1 ~ p2 ~ p3 ~ p4 ~ p5 ^^ { case x1 ~ x2 ~ x3 ~ x4 ~ x5 => x1 + x2 + x3 + x4 + x5 }
  def seq(p1: => Parser[String], p2: => Parser[String], p3: => Parser[String], p4: => Parser[String], p5: => Parser[String], p6: => Parser[String]): Parser[String] =
    p1 ~ p2 ~ p3 ~ p4 ~ p5 ~ p6 ^^ { case x1 ~ x2 ~ x3 ~ x4 ~ x5 ~ x6 => x1 + x2 + x3 + x4 + x5 + x6 }

  def strOpt(parser: => Parser[String]): Parser[String] = parser | STR_MATCH

  lazy val Skip: Parser[String] = ((WhiteSpace | LineTerminator | Comment)*) ^^ { _.mkString }
  lazy val NoLineTerminator: NodeParser[String] = new NodeParser(first => strNoLineTerminator, emptyFirst)
  lazy val strNoLineTerminator: Parser[String] = STR_MATCH <~ +(Skip.filter(s => lines.findFirstIn(s).isEmpty))
  def term(name: String, nt: Parser[String]): NodeParser[String] = new NodeParser(first => Skip ~> nt <~ Skip <~ +first.getParser, FirstTerms() + (name -> nt))
  def term(t: String): NodeParser[String] = new NodeParser(first => { Skip ~> t <~ Skip <~ +first.getParser }, FirstTerms() + t)

  lazy val emptyFirst: FirstTerms = FirstTerms(ts = Set(""))
  case class FirstTerms(ts: Set[String] = Set(), nts: Map[String, Parser[String]] = Map()) {
    def +(that: FirstTerms): FirstTerms = FirstTerms(this.ts ++ that.ts, this.nts ++ that.nts)
    def +(t: String): FirstTerms = copy(ts = ts + t)
    def +(nt: (String, Parser[String])): FirstTerms = copy(nts = nts + nt)
    def ~(that: => FirstTerms): FirstTerms =
      if (this.ts contains "") FirstTerms(this.ts - "" ++ that.ts, this.nts ++ that.nts)
      else this
    def getParser: Parser[String] = (((STR_MISMATCH: Parser[String]) /: ts)(_ | _) /: nts)(_ | _._2)
    override def toString: String = (ts ++ nts.map(_._1)).map("\"" + _ + "\"").mkString("[", ", ", "]")
  }

  lazy val MATCH: NodeParser[String] = new NodeParser(first => "" <~ +first.getParser, FirstTerms() + "")
  lazy val MISMATCH: NodeParser[Nothing] = new NodeParser(first => failure(""), FirstTerms())

  class CachedReader(reader: Reader[Char]) extends Reader[Char] { outer =>
    private[ASTParsers] val cache = mutable.HashMap.empty[(NodeParser[_], FirstTerms, Position), ParseResult[_]]
    override def source = reader.source
    override def offset = reader.offset
    def first: Char = reader.first
    def rest: Reader[Char] = new CachedReader(reader.rest) {
      override private[ASTParsers] val cache = outer.cache
    }

    def pos: Position = reader.pos
    def atEnd: Boolean = reader.atEnd
  }

  class NodeParser[+T](
      val parser: FirstTerms => Parser[T],
      val first: FirstTerms
  ) {
    def ~[U](that: => NodeParser[U]): NodeParser[~[T, U]] =
      new NodeParser(first => this.parser(that.first ~ first) ~ that.parser(first), this.first ~ that.first)

    def ~>[U](that: => NodeParser[U]): NodeParser[U] =
      new NodeParser(first => this.parser(that.first ~ first) ~> that.parser(first), this.first ~ that.first)

    def <~[U](that: => NodeParser[U]): NodeParser[T] =
      new NodeParser(first => this.parser(that.first ~ first) <~ that.parser(first), this.first ~ that.first)

    def |[U >: T](that: NodeParser[U]): NodeParser[U] =
      if (that eq MISMATCH) this
      else new NodeParser(first => this.parser(first) | that.parser(first), this.first + that.first)

    def ^^[U](f: T => U): NodeParser[U] =
      new NodeParser(first => this.parser(first) ^^ f, this.first)

    def ^^^[U](v: => U): NodeParser[U] =
      new NodeParser(first => this.parser(first) ^^^ v, this.first)

    def apply(first: FirstTerms, in: CachedReader): ParseResult[T] = parser(first)(in)

    def unary_-(): NodeParser[Unit] =
      new NodeParser(first => -parser(first), emptyFirst)

    def unary_+(): NodeParser[Unit] =
      new NodeParser(first => +parser(first), emptyFirst)
  }

  def phrase[T](p: => NodeParser[T]): NodeParser[T] =
    new NodeParser(first => phrase(p.parser(first)), p.first)

  def opt[T](p: => NodeParser[T]): NodeParser[Option[T]] =
    new NodeParser(first => opt(p.parser(first)), p.first + "")

  /** Parse some prefix of reader `in` with parser `p`. */
  def parse[T](p: NodeParser[T], in: Reader[Char]): ParseResult[T] =
    p(emptyFirst, new CachedReader(in))

  /** Parse some prefix of character sequence `in` with parser `p`. */
  def parse[T](p: NodeParser[T], in: java.lang.CharSequence): ParseResult[T] =
    parse(p, new CharSequenceReader(in))

  /** Parse some prefix of reader `in` with parser `p`. */
  def parse[T](p: NodeParser[T], in: java.io.Reader): ParseResult[T] =
    parse(p, new PagedSeqReader(PagedSeq.fromReader(in)))

  /** Parse all of reader `in` with parser `p`. */
  def parseAll[T](p: NodeParser[T], in: Reader[Char]): ParseResult[T] =
    parse(phrase(p), in)

  /** Parse all of reader `in` with parser `p`. */
  def parseAll[T](p: NodeParser[T], in: java.io.Reader): ParseResult[T] =
    parse(phrase(p), in)

  /** Parse all of character sequence `in` with parser `p`. */
  def parseAll[T](p: NodeParser[T], in: java.lang.CharSequence): ParseResult[T] =
    parse(phrase(p), in)

  var keepLog: Boolean = true
  def log[T](p: NodeParser[T])(name: String): NodeParser[T] = new NodeParser(first => Parser { rawIn =>
    val in = rawIn.asInstanceOf[CachedReader]
    val stopMsg = s"trying $name with $first at [${in.pos}] \n\n${in.pos.longString}\n"
    if (keepLog) stop(stopMsg) match {
      case "q" =>
        keepLog = false
        p(first, in)
      case "j" =>
        keepLog = false
        val r = p(first, in)
        println(name + " --> " + r)
        keepLog = true
        r
      case _ =>
        val r = p(first, in)
        println(name + " --> " + r)
        r
    }
    else p(first, in)
  }, p.first)

  private def stop(msg: String): String = {
    println(msg)
    scala.io.StdIn.readLine
  }

  type P[+T] = List[Boolean] => NodeParser[T]
  type R[T] = List[Boolean] => NodeParser[T => T]
  protected def memo[T](f: P[T]): P[T] = {
    val cache = mutable.Map.empty[List[Boolean], NodeParser[T]]
    args => cache.getOrElse(args, {
      val p = f(args)
      val parser: NodeParser[T] = new NodeParser(first => Parser { rawIn =>
        val in = rawIn.asInstanceOf[CachedReader]
        val key = (p, first, in.pos)
        in.cache.get(key) match {
          case Some(res) => res.asInstanceOf[ParseResult[T]]
          case None =>
            val res = p(first, in)
            in.cache.update(key, res)
            res
        }
      }, p.first)
      cache.update(args, parser)
      parser
    })
  }

  val Script: P[Script]

  def apply(filename: String): Script =
    parseAll(term("") ~> Script(Nil), fileReader(filename)).get

  def fromString(str: String): Script =
    parseAll(term("") ~> Script(Nil), str).get

  val rules: Map[String, P[AST]]
}
