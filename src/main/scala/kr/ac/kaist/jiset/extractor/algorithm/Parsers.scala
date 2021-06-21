package kr.ac.kaist.jiset.extractor.algorithm

import scala.util.parsing.combinator._

// common parsers
trait Parsers extends RegexParsers {
  lazy val word = "\\w+".r
}
