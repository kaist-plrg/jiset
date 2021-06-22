package kr.ac.kaist.jiset.parser

import java.io._
import java.nio.charset.Charset
import kr.ac.kaist.jiset.util.Useful._
import scala.util.parsing.combinator.{ JavaTokenParsers, RegexParsers }

trait BasicParsers extends JavaTokenParsers with RegexParsers {
  // treat comments as white spaces
  override protected val whiteSpace = """(\s|//.*)+""".r

  // parse from file
  def fromFileWithParser[T](f: String, parser: Parser[T]): T = {
    var fileName = new File(f).getCanonicalPath
    val fs = new FileInputStream(new File(f))
    val sr = new InputStreamReader(fs, Charset.forName("UTF-8"))
    val in = new BufferedReader(sr)
    val result = errHandle(parseAll(parser, in))
    in.close; sr.close; fs.close
    result
  }

  // parse with error message
  def errHandle[T](result: ParseResult[T]): T = result match {
    case Success(result, _) => result
    case err => error(s"[Parser] $err")
  }

  // parse
  def parse[T](str: String)(implicit parser: Parser[T]): T =
    errHandle(parseAll(parser, str))

  // string literal
  lazy val string = ("\"[\u0000-\u000F]\"".r | stringLiteral) ^^ {
    case s => StringContext processEscapes s.substring(1, s.length - 1)
  }

  // any word
  lazy val word = "\\w+".r

  // integer
  lazy val int = "\\d+".r ^^ { _.toInt }
}
