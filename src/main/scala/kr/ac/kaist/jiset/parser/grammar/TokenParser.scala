package kr.ac.kaist.jiset.parser.grammar

import kr.ac.kaist.jiset.spec.grammar._

// Token parsers
trait TokenParser extends TokenParsers {
  def apply(str: String): Token = parseAll(token, str).get
}
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
