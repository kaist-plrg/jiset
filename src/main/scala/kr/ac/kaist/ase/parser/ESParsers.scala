package kr.ac.kaist.ase.parser

import kr.ac.kaist.ase.model.{ AST, Script }
import kr.ac.kaist.ase.util.Useful.cached
import kr.ac.kaist.ase.{ DEBUG_PARSER, DEBUG_SEMI_INSERT, LINE_SEP }
import scala.util.parsing.input._

trait ESParsers extends LAParsers {
  // data containers
  case class Container(
    var rightmostFailedPos: Option[Position] = None
  )
  def emptyContainer = Container()

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

  // parser that supports automatic semicolon insertions
  override def parse[T](p: LAParser[T], in: Reader[Char]): ParseResult[T] = {
    val reader = new ContainerReader(in)
    p(emptyFirst, reader) match {
      case (f: Failure) => insertSemicolon(reader) match {
        case Some(str) => parse(p, str)
        case None => f
      }
      case r => r
    }
  }

  // record data
  override def record[T](parser: Parser[T], in: ContainerReader[Char]): ParseResult[T] = {
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

  // memoization of parametric rules
  def memo[T](f: ESParser[T]): ESParser[T] = cached(args => memo(f(args)))

  // main parsers
  type ESParser[T] = List[Boolean] => LAParser[T]

  // resolve left recursions
  type FLAParser[T] = LAParser[T => T]
  def resolveLL[T](f: LAParser[T], s: FLAParser[T]): LAParser[T] = {
    lazy val p: FLAParser[T] = s ~ p ^^ { case b ~ f => (x: T) => f(b(x)) } | MATCH ^^^ { (x: T) => x }
    f ~ p ^^ { case a ~ f => f(a) }
  }

  // script parsers
  val Script: ESParser[Script]

  // no LineTerminator parser
  lazy val NoLineTerminator: LAParser[String] = log(new LAParser(
    follow => strNoLineTerminator,
    () => emptyFirst
  ))("NoLineTerminator")

  // all rules
  val rules: Map[String, ESParser[AST]]
}
