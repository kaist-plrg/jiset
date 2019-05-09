package kr.ac.kaist.ase.parser

import java.io.Reader
import scala.util.parsing.combinator._

trait ParseTo[T] { this: RegexParsers =>
  private def get[U](rule: Parser[U], reader: Reader): U = parseAll(rule, reader) match {
    case Success(v, _) => v
    case e => scala.sys.error(e.toString)
  }
  def apply(reader: Reader): T = get(rule, reader)
  def getList(reader: Reader): List[T] = get(rule*, reader)
  val rule: Parser[T]
}
