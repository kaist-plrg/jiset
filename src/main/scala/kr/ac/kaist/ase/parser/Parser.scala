package kr.ac.kaist.ase.parser

import java.io.{ Reader, StringReader }
import kr.ac.kaist.ase.error.NoParseRule
import scala.reflect.ClassTag

object Parser {
  def apply[T](parser: ASEParsers[T], reader: Reader): T = parser(reader)
  def getList[T](parser: ASEParsers[T], reader: Reader): List[T] = parser.getList(reader)
}
