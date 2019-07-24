package kr.ac.kaist.ase.parser

import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.{ DEBUG_PARSER, DEBUG_SEMI_INSERT, LINE_SEP }
import scala.collection.mutable
import scala.language.reflectiveCalls
import scala.util.parsing.input._

trait LA1Parsers extends Lexer {
  // parsing case
  case class ParseCase[+T](parser: LA1Parser[T], first: FirstTerms, pos: Position)

  // container
  type Container <: {
    val cache: mutable.Map[ParseCase[_], ParseResult[_]]
  }

  // terminal parsers
  def term(name: String, nt: Lexer): LA1Parser[String] = log(new LA1Parser(first => Skip ~> nt <~ +(Skip ~ first.getParser), FirstTerms() + (name -> nt)))(name)
  def term(t: String): LA1Parser[String] = log(new LA1Parser(first => {
    Skip ~> {
      if (parseAll("[a-z]+", t).isEmpty) t
      else t <~ not(IDContinue)
    } <~ +(Skip <~ first.getParser)
  }, FirstTerms() + t))(t)

  // first terms
  case class FirstTerms(possibleEmpty: Boolean = false, ts: Set[String] = Set(), nts: Map[String, Lexer] = Map()) {
    def makeEmptyPossible: FirstTerms = copy(possibleEmpty = true)
    def +(that: FirstTerms): FirstTerms = FirstTerms(this.possibleEmpty || that.possibleEmpty, this.ts ++ that.ts, this.nts ++ that.nts)
    def +(t: String): FirstTerms = copy(ts = ts + t)
    def +(nt: (String, Lexer)): FirstTerms = copy(nts = nts + nt)
    def ~(that: => FirstTerms): FirstTerms =
      if (possibleEmpty) FirstTerms(that.possibleEmpty, this.ts ++ that.ts, this.nts ++ that.nts)
      else this
    def getParser: Lexer = Parser { rawIn =>
      val base =
        if (possibleEmpty) phrase("")
        else failure("")
      val t = TERMINAL.filter(ts contains _)
      record(
        ((base | t) /: nts)(_ | _._2),
        rawIn.asInstanceOf[ContainerReader[Char]]
      )
    }
    override def toString: String = (ts ++ nts.map(_._1) ++ (if (possibleEmpty) List("") else Nil)).map("\"" + _ + "\"").mkString("[", ", ", "]")
  }

  // empty first terms
  lazy val emptyFirst: FirstTerms = FirstTerms(possibleEmpty = true)

  // no first terms
  lazy val noFirst: FirstTerms = FirstTerms()

  lazy val MATCH: LA1Parser[String] = log(new LA1Parser(first => "" <~ +(Skip ~ first.getParser), emptyFirst))("MATCH")
  lazy val MISMATCH: LA1Parser[Nothing] = log(new LA1Parser(first => failure(""), noFirst))("MISMATCH")

  class LA1Parser[+T](
      val parser: FirstTerms => Parser[T],
      val first: FirstTerms
  ) {
    def ~[U](that: => LA1Parser[U]): LA1Parser[~[T, U]] =
      new LA1Parser(first => this.parser(that.first ~ first) ~ that.parser(first), this.first ~ that.first)

    def ~>[U](that: => LA1Parser[U]): LA1Parser[U] =
      new LA1Parser(first => this.parser(that.first ~ first) ~> that.parser(first), this.first ~ that.first)

    def <~[U](that: => LA1Parser[U]): LA1Parser[T] =
      new LA1Parser(first => this.parser(that.first ~ first) <~ that.parser(first), this.first ~ that.first)

    def |[U >: T](that: LA1Parser[U]): LA1Parser[U] =
      if (that eq MISMATCH) this
      else new LA1Parser(first => this.parser(first) | that.parser(first), this.first + that.first)

    def ^^[U](f: T => U): LA1Parser[U] =
      new LA1Parser(first => this.parser(first) ^^ f, this.first)

    def ^^^[U](v: => U): LA1Parser[U] =
      new LA1Parser(first => this.parser(first) ^^^ v, this.first)

    def apply(first: FirstTerms, in: ContainerReader[Char]): ParseResult[T] = parser(first)(in)

    def unary_-(): LA1Parser[Unit] =
      new LA1Parser(first => -parser(first), emptyFirst)

    def unary_+(): LA1Parser[Unit] =
      new LA1Parser(first => +parser(first), emptyFirst)

    def ? = new LA1Parser(first => opt(parser(first)), first.makeEmptyPossible)
  }

  // optional parsers
  def opt[T](p: => LA1Parser[T]): LA1Parser[Option[T]] =
    new LA1Parser(first => opt(p.parser(first)), p.first.makeEmptyPossible)

  // Parse charater reader `in` with parser `p`
  def parse[T](p: LA1Parser[T], in: Reader[Char]): ParseResult[T] = {
    p(emptyFirst, new ContainerReader(in))
  }

  // Parse character sequence `in` with parser `p`
  def parse[T](p: LA1Parser[T], in: java.lang.CharSequence): ParseResult[T] =
    parse(p, new CharSequenceReader(in))

  // Parse reader `in` with parser `p`
  def parse[T](p: LA1Parser[T], in: java.io.Reader): ParseResult[T] =
    parse(p, new PagedSeqReader(PagedSeq.fromReader(in)))

  // logging
  var keepLog: Boolean = true
  def log[T](p: LA1Parser[T])(name: String): LA1Parser[T] = if (!DEBUG_PARSER) p else new LA1Parser(first => Parser { rawIn =>
    val in = rawIn.asInstanceOf[ContainerReader[Char]]
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

  // stop message
  protected def stop(msg: String): String = {
    println(msg)
    scala.io.StdIn.readLine
  }

  // memoization of lookahead parsers
  protected def memo[T](p: LA1Parser[T]): LA1Parser[T] = new LA1Parser(first => Parser { rawIn =>
    val in = rawIn.asInstanceOf[ContainerReader[Char]]
    val c = ParseCase(p, first, in.pos)
    val container = in.container
    container.cache.get(c) match {
      case Some(res) => res.asInstanceOf[ParseResult[T]]
      case None =>
        val res = record(p.parser(first), in)
        container.cache.update(c, res)
        res
    }
  }, p.first)

  // record parsing process
  protected def record[T](parser: Parser[T], in: ContainerReader[Char]): ParseResult[T] = parser(in)

  // terminal lexer
  protected val TERMINAL: Lexer
}
