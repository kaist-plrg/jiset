package kr.ac.kaist.jiset.spec

import scala.util.parsing.combinator._

// common parsers
trait Parsers extends RegexParsers {
  lazy val word = "\\w+".r
  lazy val params: Parser[List[String]] = "[" ~> repsep(pWord, ",") <~ "]"
  lazy val cWord = "[A-Z]\\w+".r
  lazy val pWord = "[?|\\+|~]*\\w+".r
}

// Token parsers
trait TokenParsers extends Parsers {
  // butnot tokens
  lazy val butnot = (nt <~ ("but not" <~ opt("one of"))) ~ rep(token <~ opt("or")) ^^ {
    case base ~ cases => ButNot(base, cases)
  }

  // lookahead tokens
  lazy val containsSymbol = ("!=" | "<!" | "==" | "<") ^^ {
    case "!=" | "<!" => false
    case "==" | "<" => true
    case _ => ??? // impossible
  }
  lazy val laElem: Parser[List[Token]] = rep(token)
  lazy val laList = opt("{") ~> repsep(laElem, ",") <~ opt("}")
  lazy val lookahead = "[lookahead " ~> containsSymbol ~ laList <~ "]" ^^ {
    case b ~ cases => Lookahead(b, cases)
  }

  // terminals
  lazy val term = "`" ~> ("[^`]+".r | "`") <~ "`" ^^ { Terminal(_) }

  // non-terminals
  lazy val nt = cWord ~ opt(params) ~ opt("?") ^^ {
    case n ~ Some(args) ~ Some(_) => NonTerminal(n, args, true)
    case n ~ Some(args) ~ None => NonTerminal(n, args, false)
    case n ~ None ~ Some(_) => NonTerminal(n, Nil, true)
    case n ~ None ~ None => NonTerminal(n, Nil, false)
  }

  // unicode
  lazy val unicode = "<" ~> word <~ ">" ^^ { Unicode(_) }

  // empty
  val empty = "[empty]" ^^ { _ => EmptyToken }

  // no line terminator
  val nlt = "\\[no [\\|]?LineTerminator[\\|]? here\\]".r ^^ { _ => NoLineTerminatorToken }

  // unicode any
  val uniAny = "any Unicode code point$".r ^^ { _ => UnicodeAny }

  // unicode id start
  val uniStart =
    "any Unicode code point with the Unicode property “ID_Start”$".r ^^ { _ => UnicodeIdStart }

  // unicode id continue
  val uniCont =
    "any Unicode code point with the Unicode property “ID_Continue”$".r ^^ { _ => UnicodeIdStart }

  // unicode lead surrogate
  val uniLeadSur =
    "any Unicode code point in the inclusive range 0xD800 to 0xDBFF" ^^ { _ => UnicodeLeadSurrogate }

  // unicode trail surrogate
  val uniTrailSur =
    "any Unicode code point in the inclusive range 0xDC00 to 0xDFFF" ^^ { _ => UnicodeTrailSurrogate }

  // unicode code point
  val uniCodePoint = ">" ~> (uniLeadSur | uniTrailSur | uniStart | uniCont | uniAny)

  // manual cases
  val manual = empty | nlt | uniCodePoint

  // tokens
  lazy val token: Parser[Token] = manual | unicode | butnot | lookahead | nt | term
}

// Rhs parsers
trait RhsParsers extends TokenParsers {
  lazy val rhs: Parser[Rhs] = opt(constraints) ~ rep(token) ^^ {
    case Some(cond) ~ tokens => Rhs(tokens, cond)
    case None ~ tokens => Rhs(tokens, "")
  }
  lazy val constraints = "[" ~> "[+|~]".r ~ word <~ "]" ^^ {
    case "+" ~ c => "p" + c
    case "~" ~ c => "!p" + c
    case _ => ??? // impossible
  }
}

// Lhs parsers
trait LhsParsers extends Parsers {
  lazy val lhs: Parser[Lhs] = word ~ opt(params) <~ "[:]+".r ^^ {
    case n ~ None => Lhs(n, Nil)
    case n ~ Some(params) => Lhs(n, params)
  }
}

// Production parsers
trait ProductionParsers extends LhsParsers with RhsParsers {
  def parse(lines: List[String]): Production = lines match {
    case lhsStr :: rhsStrList => {
      val lhs = Lhs(lhsStr)
      // create rhsList
      var rhsList = rhsStrList.map(Rhs(_))
      // handle oneof
      if (lhsStr.trim.endsWith("one of")) {
        rhsList = rhsList.foldLeft(List.empty[Rhs]) {
          case (acc, Rhs(tokens, cond)) =>
            acc ++ tokens.map(t => Rhs(t :: Nil, cond))
        }
      }
      Production(lhs, rhsList)
    }
    case Nil => ??? // impossible
  }
}
