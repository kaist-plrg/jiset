package kr.ac.kaist.ase.spec

import spray.json._

// ECMAScript grammars
case class Grammar(
  lexProds: List[Production],
  prods: List[Production]
)

// productions
case class Production(
  lhs: Lhs,
  rhsList: List[Rhs]
)

// left-hand-sides
case class Lhs(name: String, params: List[String])

// right-hand-sides
case class Rhs(
  tokens: List[Token],
  cond: String
)

// tokens
trait Token
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
