package kr.ac.kaist.jiset.parser

import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.error._
import kr.ac.kaist.jiset.js.ast.Script
import kr.ac.kaist.jiset.util.PureUseful._
import kr.ac.kaist.jiset.{ INTERACTIVE, LINE_SEP }
import scala.collection.mutable
import scala.util.matching.Regex
import scala.util.parsing.combinator._
import scala.util.parsing.input._

trait ESParsers extends LAParsers {
  // automatic semicolon insertion
  def insertSemicolon(reader: EPackratReader[Char]): Option[String] = {
    reader.container.rightmostFailedPos match {
      case Some((pos, rev)) =>
        val source = reader.source.toString
        val line = pos.line - 1
        val column = pos.column - 1
        val lines = source.replace("\r\n", "\n").split(Array('\n', '\r'))
        val revStr = rev.mkString

        // Interactive debugging for semicolon insertion
        if (INTERACTIVE && keepLog) {
          lines.zipWithIndex.foreach {
            case (x, i) => println(f"$i%4d: $x")
          }
          stop(s"line: $line, column: $column") match {
            case "q" => keepLog = false
            case _ =>
          }
        }

        lazy val curLine = lines(line)
        lazy val curChar = curLine.charAt(column)

        // insert semicolon right before the offending token
        lazy val insert = Some({
          if (line < lines.length && column < curLine.length) {
            val (pre, post) = curLine.splitAt(column)
            lines(line) = pre + ';' + post
            lines.mkString("\n")
          } else lines.mkString("\n") + "\n;"
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
        if (!parse(strNoLineTerminator, revStr).successful) return insert

        // 1-2. The offending token is '}'
        if (curChar == '}') return insert

        // 1-3. The previous token is ')' and the inserted semicolon would then be
        //      parsed as the terminating semicolon of a do-while statement (13.7.2).
        reader.container.rightmostDoWhileClose match {
          case Some(doWhilePos) if doWhilePos == pos => return insert
          case _ =>
        }

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
        if (t.matches("[a-z]+")) t <~ not(IDContinue)
        else t
      } <~ +follow.parser
    }, FirstTerms() + t))(t)
  }
  val doWhileCloseT: LAParser[String] = {
    val p = t(")")
    new LAParser(follow => Parser { rawIn =>
      val in = rawIn.asInstanceOf[EPackratReader[Char]]
      val c = ParseCase(p, follow, in.pos)
      val container = in.container
      container.cache.get(c) match {
        case Some(res) => res.asInstanceOf[ParseResult[String]]
        case None =>
          val res = recordDoWhileClose(p.parser(follow), in)
          container.cache += c -> res
          res
      }
    }, p.first)
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
    val init: Either[ParseResult[T], Reader[Char]] = Right(in)
    (0 until MAX_ADDITION).foldLeft(init) {
      case (Right(in), _) =>
        val reader = new EPackratReader(in)
        p(emptyFirst, reader) match {
          case (f: Failure) => insertSemicolon(reader) match {
            case Some(str) => Right(new CharSequenceReader(str))
            case None => Left(f)
          }
          case r => Left(r)
        }
      case (res, _) => res
    } match {
      case Left(res) => res
      case _ => throw TooManySemicolonInsertion(MAX_ADDITION)
    }
  }

  // ECMAScript parsers
  type ESParser[+T] = List[Boolean] => LAParser[T]

  // memoization of parametric rules
  def memo[T](f: ESParser[T]): ESParser[T] = cached(args => memo(f(args)))

  // resolve left recursions
  type FLAParser[T] = LAParser[T => T]
  def resolveLR[T](f: LAParser[T], s: FLAParser[T]): LAParser[T] = {
    lazy val p: FLAParser[T] = s ~ p ^^ { case b ~ f => (x: T) => f(b(x)) } | MATCH ^^^ { (x: T) => x }
    f ~ p ^^ { case a ~ f => f(a) }
  }

  // record right-most faield positions
  protected def record[T](
    parser: Parser[T],
    in: EPackratReader[Char]
  ): ParseResult[T] = {
    val container = in.container
    val res = parser(in)
    (res, container.rightmostFailedPos) match {
      case (f @ Failure(_, cur: EPackratReader[_]), Some((origPos, _))) if origPos < cur.pos =>
        container.rightmostFailedPos = Some((cur.pos, cur.rev))
      case (f @ Failure(_, cur: EPackratReader[_]), None) =>
        container.rightmostFailedPos = Some((cur.pos, cur.rev))
      case _ =>
    }
    res
  }

  // record right-most do-while close token positions
  protected def recordDoWhileClose[T](
    parser: Parser[T],
    in: EPackratReader[Char]
  ): ParseResult[T] = {
    val container = in.container
    val res = parser(in)
    (res, container.rightmostDoWhileClose) match {
      case (f @ Failure(_, cur: EPackratReader[_]), Some(origPos)) if origPos < cur.pos =>
        container.rightmostDoWhileClose = Some(cur.pos)
      case (f @ Failure(_, cur: EPackratReader[_]), None) =>
        container.rightmostDoWhileClose = Some(cur.pos)
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
