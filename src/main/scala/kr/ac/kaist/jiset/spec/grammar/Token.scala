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
}
case class Terminal(term: String) extends Token
case class NonTerminal(
    name: String,
    args: List[String],
    optional: Boolean
) extends Token
case class ButNot(
    base: Token,
    cases: List[Token]
) extends Token
case class Lookahead(
    contains: Boolean,
    cases: List[List[Token]]
) extends Token
case class Unicode(code: String) extends Token
case object EmptyToken extends Token
case object NoLineTerminatorToken extends Token
case object UnicodeAny extends Token
case object UnicodeIdStart extends Token
case object UnicodeIdContinue extends Token
case object UnicodeLeadSurrogate extends Token
case object UnicodeTrailSurrogate extends Token
