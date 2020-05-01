package kr.ac.kaist.jiset.spec

import spray.json._

// ECMAScript grammars
case class Grammar(
    var lexProds: List[Production],
    var prods: List[Production]
)

// productions
case class Production(
    var lhs: Lhs,
    var rhsList: List[Rhs]
)

// left-hand-sides
case class Lhs(
    var name: String,
    var params: List[String]
)

// right-hand-sides
case class Rhs(
    var tokens: List[Token],
    var cond: String
)

// tokens
trait Token
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
