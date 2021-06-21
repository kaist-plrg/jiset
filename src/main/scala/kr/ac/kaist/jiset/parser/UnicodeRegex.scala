package kr.ac.kaist.jiset.parser

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.util.Useful._
import scala.util.parsing.combinator.RegexParsers
import spray.json._

trait UnicodeRegex { _: RegexParsers =>
  // special characters
  lazy val ZWNJ = "\u200C"
  lazy val ZWJ = "\u200D"
  lazy val ZWNBSP = "\uFEFF"

  lazy val TAB = "\u0009"
  lazy val VT = "\u000B"
  lazy val FF = "\u000C"
  lazy val SP = "\u0020"
  lazy val NBSP = "\u00A0"
  lazy val USP = "[\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000]".r

  lazy val LF = "\u000A"
  lazy val CR = "\u000D"
  lazy val LS = "\u2028"
  lazy val PS = "\u2029"

  lazy val lines = "[\u000A\u000D\u2028\u2029]".r

  lazy val Unicode = "(?s).".r

  lazy val Comment = """/\*+[^*]*\*+(?:[^/*][^*]*\*+)*/|//[^\u000A\u000D\u2028\u2029]*""".r

  private val IDStartSet: Set[Int] = readCodePointSet(ID_START_JSON)
  val IDStart = Unicode.filter(s => IDStartSet contains toCodePoint(s))
  private val IDContinueSet: Set[Int] = readCodePointSet(ID_CONTINUE_JSON)
  val IDContinue = Unicode.filter(s => IDContinueSet contains toCodePoint(s))

  private def readCodePointSet(filename: String): Set[Int] = {
    readFile(filename).parseJson match {
      case JsArray(vs) => vs.flatMap(_ match {
        case JsArray(Vector(JsNumber(l), JsNumber(r))) =>
          (l.toInt to r.toInt).toList
        case JsNumber(cp) =>
          List(cp.toInt)
        case _ =>
          Nil
      }).toSet
      case _ => Set()
    }
  }

  private def toCodePoint(str: String): Int =
    str.codePoints.toArray.foldLeft(0)(_ * (1 << 16) + _)
}
