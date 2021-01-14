package kr.ac.kaist.jiset.spec

import scala.util.parsing.combinator._

// common parsers
trait Parsers extends RegexParsers {
  lazy val word = "\\w+".r
  lazy val params: Parser[List[String]] = "[" ~> repsep(pWord, ",") <~ "]"
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
  lazy val nt = word ~ opt(params) ~ opt("?") ^^ {
    case n ~ Some(args) ~ Some(_) => NonTerminal(n, args, true)
    case n ~ Some(args) ~ None => NonTerminal(n, args, false)
    case n ~ None ~ Some(_) => NonTerminal(n, Nil, true)
    case n ~ None ~ None => NonTerminal(n, Nil, false)
  }

  // empty
  lazy val empty = "[empty]" ^^^ EmptyToken

  // no line terminator
  lazy val nlt = "\\[no [\\|]?LineTerminator[\\|]? here\\]".r ^^^ NoLineTerminatorToken

  // unicode

  // characters
  lazy val character = (
    "<" ~> word <~ ">" ^^ { Unicode(_) } |
    ".*any.*code point.*".r ^^^ UnicodeAny |
    ".*code point.*ID_Start.*".r ^^^ UnicodeIdStart |
    ".*code point.*ID_Continue.*".r ^^^ UnicodeIdContinue |
    ".*code point.*0xD800 to 0xDBFF.*".r ^^^ UnicodeLeadSurrogate |
    ".*code point.*0xDC00 to 0xDFFF.*".r ^^^ UnicodeTrailSurrogate |
    ".*HexDigits.*> 0x10FFFF.*".r ^^^ NotCodePoint |
    ".*HexDigits.*≤ 0x10FFFF.*".r ^^^ CodePoint |
    ".*Hex4Digits.*0xD800 to 0xDBFF.*".r ^^^ HexLeadSurrogate |
    ".*Hex4Digits.*0xDC00 to 0xDFFF.*".r ^^^ HexTrailSurrogate |
    ".*Hex4Digits.*not.*0xD800 to 0xDFFF.*".r ^^^ HexNonSurrogate
  )

  // special cases
  lazy val special = empty | nlt | character

  // tags
  lazy val tag = "#" ~ word

  // tokens
  lazy val token: Parser[Token] = special | butnot | lookahead | nt | term
}

// Rhs parsers
trait RhsParsers extends TokenParsers {
  lazy val rhs: Parser[Rhs] = opt(cond) ~ rep(token) <~ opt(tag) ^^ {
    case cond ~ tokens => Rhs(tokens, cond)
  }
  lazy val cond = "[" ~> "[+~]".r ~ word <~ "]" ^^ {
    case s ~ c => RhsCond(c, s == "+")
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
  lazy val oneof: Parser[Boolean] = opt("one of") ^^ { !_.isEmpty }
  lazy val lhsLine = lhs ~ oneof ~ opt(rhs)
  def parse(lines: List[String]): Production = lines match {
    case lhsStr :: rhsStrList => {
      val lhs ~ split ~ rhsOpt = parseAll(lhsLine, lhsStr).get
      // create rhsList
      var rhsList = rhsStrList.map(Rhs(_))
      rhsOpt.map(rhsList ::= _)
      // handle `one of`
      if (split) rhsList = rhsList.flatMap {
        case Rhs(tokens, cond) => tokens.map(t => Rhs(List(t), cond))
      }
      Production(lhs, rhsList)
    }
    case Nil => ??? // impossible
  }
}
