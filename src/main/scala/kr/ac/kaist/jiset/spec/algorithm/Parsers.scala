package kr.ac.kaist.jiset.spec.algorithm

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.ires.ir.Parser._
import scala.util.parsing.combinator._

// common parsers
trait Parsers extends RegexParsers {
  lazy val word = "\\w+".r
}

// head parsers
trait HeadParsers extends Parsers {
  lazy val name = "[a-zA-Z]*".r
  lazy val field = (
    "." ~> name ^^ { EStr(_) } |
    "[" ~ "@@" ~> name <~ "]" ^^ { x => parseExpr("SYMBOL_" + x) }
  )
  lazy val ref = name ~ rep(field) ^^ {
    case b ~ fs => fs.foldLeft[Ref](RefId(Id(b))) {
      case (b, f) => RefProp(b, f)
    }
  }

  lazy val paramString =
    "_[a-zA-Z0-9]+_".r ^^ { case s => s.substring(1, s.length - 1) }
  lazy val params: Parser[List[Param]] = (
    "[" ~ opt(",") ~> paramString ~ params <~ "]" ^^ { case x ~ ps => Param(x, true) :: ps } |
    opt(",") ~> paramString ~ params ^^ { case x ~ ps => Param(x, false) :: ps } |
    "" ^^^ Nil
  )
  lazy val paramList = "(" ~> params <~ ")"
}
