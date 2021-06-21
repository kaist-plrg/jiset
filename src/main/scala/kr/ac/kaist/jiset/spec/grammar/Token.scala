package kr.ac.kaist.jiset.spec.grammar.token

import kr.ac.kaist.jiset.util.Useful._

// ECMAScript grammar tokens
trait Token {
  // normalize tokens
  def norm: Option[NonTerminal] = this match {
    case ButNot(base, _) => base.norm
    case (t: NonTerminal) => Some(t)
    case _ => None
  }

  // filter non-terminals
  def getNT: Option[NonTerminal] = this match {
    case (nt: NonTerminal) => Some(nt)
    case ButNot(base, _) => base.getNT
    case _ => None
  }

  // conversion to string
  override def toString: String = this match {
    case Terminal(term) =>
      s"`$term`"
    case NonTerminal(name, args, optional) =>
      val argsStr = if (args.isEmpty) "" else args.mkString("[", ", ", "]")
      val optionalStr = if (optional) "?" else ""
      s"$name$argsStr$optionalStr"
    case ButNot(base, cases) =>
      val casesStr = cases.mkString(" or ")
      s"$base but not $casesStr"
    case Lookahead(contains, cases) =>
      val containsStr = if (contains) "<" else "<!"
      val casesStr = cases.map(_.mkString(" ")).mkString("{", ", ", "}")
      s"[lookahead $containsStr $casesStr]"
    case EmptyToken =>
      s"[empty]"
    case NoLineTerminatorToken =>
      s"[no LineTerminator here]"
    case (char: Character) =>
      s"<${char.name}>"
  }
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
abstract class Character(val name: String) extends Token
object Character {
  val predef = List(
    UnicodeAny, UnicodeIdStart, UnicodeIdContinue, UnicodeLeadSurrogate,
    UnicodeTrailSurrogate, NotCodePoint, CodePoint, HexLeadSurrogate,
    HexTrailSurrogate, HexNonSurrogate
  )
  val nameMap = (for (ch <- predef) yield ch.name -> ch).toMap
}
case class Unicode(code: String) extends Character(code)
case object UnicodeAny extends Character("UnicodeAny")
case object UnicodeIdStart extends Character("UnicodeIdStart")
case object UnicodeIdContinue extends Character("UnicodeIdContinue")
case object UnicodeLeadSurrogate extends Character("UnicodeLeadSurrogate")
case object UnicodeTrailSurrogate extends Character("UnicodeTrailSurrogate")
case object NotCodePoint extends Character("NotCodePoint")
case object CodePoint extends Character("CodePoint")
case object HexLeadSurrogate extends Character("HexLeadSurrogate")
case object HexTrailSurrogate extends Character("HexTrailSurrogate")
case object HexNonSurrogate extends Character("HexNonSurrogate")
