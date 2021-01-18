package kr.ac.kaist.jiset.spec.algorithm

import scala.util.parsing.combinator._

// common parsers
trait Parsers extends RegexParsers {
  lazy val word = "\\w+".r
}

// AlgoHead parsers
trait AlgoHeadParsers extends Parsers {
  lazy val name = "[a-zA-Z]*".r
  lazy val field = {
    "." ~> name ^^ { NormalField(_) } |
      "[" ~ "@@" ~> name <~ "]" ^^ { SymbolField(_) }
  }
  lazy val path = name ~ rep(field) ^^ { case b ~ fs => (b, fs) }
}
