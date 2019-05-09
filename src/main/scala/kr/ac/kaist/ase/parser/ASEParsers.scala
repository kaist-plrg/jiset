package kr.ac.kaist.ase.parser

import java.io.Reader
import scala.util.parsing.combinator._

trait ASEParsers[T] extends RegexParsers {
  def apply(reader: Reader): T = parse(rule, reader).get
  def getList(reader: Reader): List[T] = parse(rule*, reader).get
  val rule: Parser[T]
}
