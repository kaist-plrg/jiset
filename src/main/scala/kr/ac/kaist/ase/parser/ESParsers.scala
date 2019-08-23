package kr.ac.kaist.ase.parser

import kr.ac.kaist.ase.error.{ WrongNumberOfParserParams, TooManySemicolonInsertion }
import kr.ac.kaist.ase.model.{ Script }
import kr.ac.kaist.ase.util.Useful._
import kr.ac.kaist.ase.{ AST, Lexical, DEBUG_PARSER, DEBUG_SEMI_INSERT, LINE_SEP }
import scala.collection.mutable
import scala.util.matching.Regex
import scala.util.parsing.combinator._
import scala.util.parsing.input._

trait ESParsers extends LAParsers {
  // container with cache for LAParser and right-most failed positions
  def emptyContainer: Container = Container()
  case class Container(
    var cache: Map[ParseCase[_], ParseResult[_]] = Map(),
    var rightmostFailedPos: Option[Position] = None
  )

  // automatic semicolon insertion
  def insertSemicolon(reader: ContainerReader[Char]): Option[String] = {
    reader.container.rightmostFailedPos match {
      case Some(pos) =>
        val source = reader.source.toString
        val line = pos.line - 1
        val column = pos.column - 1
        val lines = source.split('\n')

        if (DEBUG_SEMI_INSERT) {
          println(source)
          lines.zipWithIndex.foreach {
            case (x, i) => println(f"$i%4d: $x")
          }
          stop(s"line: $line, column: $column")
        }

        lazy val curLine = lines(line)
        lazy val curChar = curLine.charAt(column)

        // insert semicolon right before the offending token
        lazy val insert = Some({
          if (line < lines.length) {
            val (pre, post) = curLine.splitAt(column)
            lines(line) = pre + ';' + post
          } else lines(lines.length - 1) = lines(lines.length - 1) + ';'
          lines.mkString("\n")
        })

        // 2. The end of the input stream of tokens is encountered
        if (line >= lines.length ||
          (line == lines.length - 1 && column == curLine.length)) return insert

        // A. Additional Rules
        // A semicolon is never inserted automatically if the semicolon
        // would then be parsed as an empty statement or if that semicolon
        // would become one of the two semicolons in the header of a for statement
        // TODO
        if (curChar == ';') return None

        // 1-1. The offending token is separated from the previous token
        //      by at least one LineTerminator
        if (parseAll(rep(WhiteSpace), curLine.splitAt(column)._1).successful &&
          line > 0 && !lines.splitAt(line)._1.forall {
            case line => parseAll(rep(WhiteSpace), line).successful
          }) return insert

        // 1-2. The offending token is '}'
        if (curChar == '}') return insert

        // 1-3. The previous token is ')' and the inserted semicolon would then be
        //      parsed as the terminating semicolon of a do-while statement (13.7.2).
        // TODO

        // 3. the restricted token is separated from the previous token
        //    by at least one LineTerminator, then a semicolon is automatically
        //    inserted before the restricted token.
        // TODO

        None
      case None => None
    }
  }

  // terminal parsers
  val t = cached[String, LAParser[String]] {
    case t => log(new LAParser(follow => {
      Skip ~> {
        if (parseAll("[a-z]+".r, t).isEmpty) t
        else t <~ not(IDContinue)
      } <~ +follow.parser
    }, FirstTerms() + t))(t)
  }
  val nt = cached[(String, Lexer), LAParser[Lexical]] {
    case (name, nt) => log(new LAParser(
      follow => (Skip ~> nt <~ +follow.parser) ^^ { case s => Lexical(name, s) },
      FirstTerms() + (name -> nt)
    ))(name)
  }
  def ntl = cached[Lexer, LAParser[Lexical]] {
    case nt => log(new LAParser(
      follow => (Skip ~> nt) ^^ { case s => Lexical("", s) },
      FirstTerms()
    ))("")
  }

  // parser that supports automatic semicolon insertions
  override def parse[T](p: LAParser[T], in: Reader[Char]): ParseResult[T] = {
    val MAX_ADDITION = 100
    val init: (Option[ParseResult[T]], Reader[Char]) = (None, in)
    (init /: (0 until MAX_ADDITION)) {
      case ((None, in), _) =>
        val reader = new ContainerReader(in)
        p(emptyFirst, reader) match {
          case (f: Failure) => insertSemicolon(reader) match {
            case Some(str) => (None, new CharSequenceReader(str))
            case None => (Some(f), reader)
          }
          case r => (Some(r), reader)
        }
      case (res, _) => res
    } match {
      case (Some(res), _) => res
      case _ => throw TooManySemicolonInsertion(MAX_ADDITION)
    }
  }

  // ECMAScript parsers
  type ESParser[+T] = List[Boolean] => LAParser[T]

  // memoization of parametric rules
  def memo[T](f: ESParser[T]): ESParser[T] = cached(args => memo(f(args)))

  // resolve left recursions
  type FLAParser[T] = LAParser[T => T]
  def resolveLL[T](f: LAParser[T], s: FLAParser[T]): LAParser[T] = {
    lazy val p: FLAParser[T] = s ~ p ^^ { case b ~ f => (x: T) => f(b(x)) } | MATCH ^^^ { (x: T) => x }
    f ~ p ^^ { case a ~ f => f(a) }
  }

  // record right-most faield positions
  protected def record[T](parser: Parser[T], in: ContainerReader[Char]): ParseResult[T] = {
    val container = in.container
    val res = parser(in)
    (res, container.rightmostFailedPos) match {
      case (f @ Failure(_, cur), Some(origPos)) if origPos < cur.pos =>
        container.rightmostFailedPos = Some(cur.pos)
      case (f @ Failure(_, cur), None) =>
        container.rightmostFailedPos = Some(cur.pos)
      case _ =>
    }
    res
  }

  // script parsers
  val Script: ESParser[Script]

  // no LineTerminator parser
  lazy val NoLineTerminator: LAParser[String] = log(new LAParser(
    follow => strNoLineTerminator,
    emptyFirst
  ))("NoLineTerminator")

  // all rules
  val rules: Map[String, ESParser[AST]]

  // get fixed length arguments
  def getArgsN(name: String, args: List[Boolean], n: Int): List[Boolean] = {
    if (args.length == n) args
    else throw WrongNumberOfParserParams(name, args)
  }
}
