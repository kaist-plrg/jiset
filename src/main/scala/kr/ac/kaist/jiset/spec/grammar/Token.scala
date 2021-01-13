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
case class Terminal(var term: String) extends Token
case class NonTerminal(
    var name: String,
    var args: List[String],
    var optional: Boolean
) extends Token
case class ButNot(
    var base: Token,
    var cases: List[Token]
) extends Token
case class Lookahead(
    var contains: Boolean,
    var cases: List[List[Token]]
) extends Token
case class Unicode(var code: String) extends Token
case object EmptyToken extends Token
case object NoLineTerminatorToken extends Token
case object UnicodeAny extends Token
case object UnicodeIdStart extends Token
case object UnicodeIdContinue extends Token
case object UnicodeLeadSurrogate extends Token
case object UnicodeTrailSurrogate extends Token
