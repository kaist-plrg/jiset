package kr.ac.kaist.jiset.spec

// ECMAScript grammar tokens
trait Token {
  def norm: Option[Token] = this match {
    case ButNot(base, _) => base.norm
    case EmptyToken | Lookahead(_, _) => None
    case t => Some(t)
  }
}
object Token extends TokenParsers {
  def apply(str: String): Token = parseAll(token, str).get
}

// terminals
case class Terminal(term: String) extends Token

// non-terminals
case class NonTerminal(
    name: String,
    args: List[String],
    optional: Boolean
) extends Token

// but-not tokens
case class ButNot(
    base: Token,
    cases: List[Token]
) extends Token

// lookahead tokens
case class Lookahead(
    contains: Boolean,
    cases: List[List[Token]]
) extends Token

// empty tokens
case object EmptyToken extends Token

// no line terminator tokens
case object NoLineTerminatorToken extends Token

// char tokens
trait Character extends Token
case class Unicode(code: String) extends Character
case object UnicodeAny extends Character
case object UnicodeIdStart extends Character
case object UnicodeIdContinue extends Character
case object UnicodeLeadSurrogate extends Character
case object UnicodeTrailSurrogate extends Character
case object NotCodePoint extends Character
case object CodePoint extends Character
case object HexLeadSurrogate extends Character
case object HexTrailSurrogate extends Character
case object HexNonSurrogate extends Character
