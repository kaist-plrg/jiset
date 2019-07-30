package kr.ac.kaist.ase.parser

import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.{ DEBUG_PARSER, DEBUG_SEMI_INSERT, LINE_SEP }
import scala.collection.mutable
import scala.language.reflectiveCalls
import scala.util.parsing.input._

trait LAParsers extends Lexer {
  val fail = failure("")
  object ParserMemoized extends Memoized[FirstTerms, Parser] {
    def failure(msg: String, i: FirstTerms): Parser[Nothing] = fail
    def isSuccess(result: Parser[_]): Boolean = result != fail
    def isProminent(old: Parser[_], temp: Parser[_]): Boolean = ???
    type Case[T] = FirstTerms => Parser[T]
  }

  type Dummy[T] = FirstTerms
  object FirstMemoized extends Memoized[LAParser[_], Dummy] {
    def failure(msg: String, i: LAParser[_]): Dummy[Nothing] = noFirst
    def isSuccess(result: Dummy[_]): Boolean = result != noFirst
    def isProminent(old: Dummy[_], temp: Dummy[_]): Boolean =
      old < temp
    type Case[T] = LAParser[_] => Dummy[T]
  }

  // Lookahaed parsers
  object LAParser {
    val getFirst: LAParser[_] => FirstTerms = p => p._first()
  }
  class LAParser[+T](
      val _parser: FirstTerms => Parser[T],
      val _first: () => FirstTerms
  ) {
    def parser(follow: FirstTerms): Parser[T] =
      ParserMemoized.memo(_parser, follow)
    def first: FirstTerms =
      FirstMemoized.memo(LAParser.getFirst, this)

    def ~[U](_that: => LAParser[U]): LAParser[~[T, U]] = {
      lazy val that = _that
      new LAParser(
        follow => this.parser(that.first ~ follow) ~ that.parser(follow),
        () => this.first ~ that.first
      )
    }

    def ~>[U](_that: => LAParser[U]): LAParser[U] = {
      lazy val that = _that
      new LAParser(
        follow => this.parser(that.first ~ follow) ~> that.parser(follow),
        () => this.first ~ that.first
      )
    }

    def <~[U](_that: => LAParser[U]): LAParser[T] = {
      lazy val that = _that
      new LAParser(
        follow => this.parser(that.first ~ follow) <~ that.parser(follow),
        () => this.first ~ that.first
      )
    }

    def |[U >: T](that: LAParser[U]): LAParser[U] = if (that eq MISMATCH) this else new LAParser(
      follow => this.parser(follow) ||| that.parser(follow),
      () => this.first + that.first
    )

    def ^^[U](f: T => U): LAParser[U] = new LAParser(
      follow => this.parser(follow) ^^ f,
      () => this.first
    )

    def ^^^[U](_v: => U): LAParser[U] = {
      lazy val v = _v
      new LAParser(
        follow => this.parser(follow) ^^^ v,
        () => this.first
      )
    }

    def apply(follow: FirstTerms, in: ContainerReader[Char]): ParseResult[T] = parser(follow)(in)

    def unary_-(): LAParser[Unit] = new LAParser(
      follow => not(parser(follow)),
      () => emptyFirst
    )

    def unary_+(): LAParser[T] = new LAParser(
      follow => guard(parser(follow)),
      () => emptyFirst
    )
  }

  // first terms
  case class FirstTerms(possibleEmpty: Boolean = false, ts: Set[String] = Set(), nts: Map[String, Lexer] = Map()) {
    def <(that: FirstTerms): Boolean = this != that && (
      (!this.possibleEmpty || that.possibleEmpty) &&
      (this.ts subsetOf that.ts) &&
      (this.nts.keySet subsetOf that.nts.keySet)
    )
    def makeEmptyPossible: FirstTerms = copy(possibleEmpty = true)
    def +(that: FirstTerms): FirstTerms = FirstTerms(this.possibleEmpty || that.possibleEmpty, this.ts ++ that.ts, this.nts ++ that.nts)
    def +(t: String): FirstTerms = copy(ts = ts + t)
    def +(nt: (String, Lexer)): FirstTerms = copy(nts = nts + nt)
    def ~(that: => FirstTerms): FirstTerms =
      if (possibleEmpty) FirstTerms(that.possibleEmpty, this.ts ++ that.ts, this.nts ++ that.nts)
      else this
    def parser: Lexer = Parser { rawIn =>
      val base =
        if (possibleEmpty) phrase(empty)
        else fail
      val t = TERMINAL.filter(ts contains _)
      record(
        Skip ~> ((base | t) /: nts)(_ | _._2),
        rawIn.asInstanceOf[ContainerReader[Char]]
      )
    }
    override def toString: String = (ts.map("\"" + _ + "\"") ++ nts.map(_._1) ++ (if (possibleEmpty) List("ε") else Nil)).mkString("[", ", ", "]")
  }

  // empty first terms
  val emptyFirst: FirstTerms = FirstTerms(possibleEmpty = true)

  // no first terms
  val noFirst: FirstTerms = FirstTerms()

  // terminal parsers
  val nt = cached[(String, Lexer), LAParser[String]] {
    case (name, nt) => log(new LAParser(
      follow => Skip ~> nt <~ +follow.parser,
      () => FirstTerms() + (name -> nt)
    ))(name)
  }
  val t = cached[String, LAParser[String]] {
    case t => log(new LAParser(follow => {
      Skip ~> {
        if (parseAll("[a-z]+".r, t).isEmpty) t
        else t <~ not(IDContinue)
      } <~ +follow.parser
    }, () => FirstTerms() + t))(t)
  }

  // always match
  val MATCH: LAParser[String] = log(new LAParser(
    follow => empty <~ +follow.parser,
    () => emptyFirst
  ))("MATCH")

  // always mismatch
  val MISMATCH: LAParser[Nothing] = log(new LAParser(
    follow => fail,
    () => noFirst
  ))("MISMATCH")

  // optional parsers
  def opt[T](p: LAParser[T]): LAParser[Option[T]] = p ^^ { Some(_) } | MATCH ^^^ None

  // Parse charater reader `in` with parser `p`
  def parse[T](p: LAParser[T], in: Reader[Char]): ParseResult[T] = {
    p(emptyFirst, new ContainerReader(in))
  }

  // Parse character sequence `in` with parser `p`
  def parse[T](p: LAParser[T], in: java.lang.CharSequence): ParseResult[T] =
    parse(p, new CharSequenceReader(in))

  // Parse reader `in` with parser `p`
  def parse[T](p: LAParser[T], in: java.io.Reader): ParseResult[T] =
    parse(p, new PagedSeqReader(PagedSeq.fromReader(in)))

  // logging
  var keepLog: Boolean = true
  def log[T](p: LAParser[T])(pname: String, args: List[Boolean] = Nil): LAParser[T] =
    if (!DEBUG_PARSER) p else new LAParser(follow => {
      val argsStr = if (args.isEmpty) "" else args.mkString("(", ", ", ")")
      val name = s"$pname$argsStr with $follow"
      Parser { rawIn =>
        val in = rawIn.asInstanceOf[ContainerReader[Char]]
        val stopMsg = s"trying $name at [${in.pos}] \n\n${in.pos.longString}\n"
        if (keepLog) stop(stopMsg) match {
          case "q" =>
            keepLog = false
            p(follow, in)
          case "j" =>
            keepLog = false
            val r = p(follow, in)
            println(name + " --> " + r)
            keepLog = true
            r
          case _ =>
            val r = p(follow, in)
            println(name + " --> " + r)
            r
        }
        else p(follow, in)
      }.named(name)
    }, () => p.first)

  // stop message
  protected def stop(msg: String): String = {
    println(msg)
    scala.io.StdIn.readLine
  }

  // memoization of lookahead parsers
  def memo[T](_p: => LAParser[T]): LAParser[T] = {
    lazy val p = _p
    new LAParser(follow => memo(p.parser(follow)), () => p.first)
  }

  // record parsing process
  protected def record[T](parser: Parser[T], in: ContainerReader[Char]): ParseResult[T] = parser(in)

  // terminal lexer
  protected val TERMINAL: Lexer
}
