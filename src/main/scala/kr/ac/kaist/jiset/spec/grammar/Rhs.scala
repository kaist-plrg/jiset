package kr.ac.kaist.jiset.spec

// ECMAScript grammar right-hand-sides
case class Rhs(
    tokens: List[Token],
    cond: String
) {
  // check whehter if tokens is a single nonterminal
  def isSingleNT: Boolean = tokens.flatMap(_.norm) match {
    case List(_: NonTerminal) => true
    case _ => false
  }
  // check non terminal
  def check(f: String => Boolean, init: Boolean, op: (Boolean, Boolean) => Boolean) =
    tokens.foldLeft(init) {
      case (b, t) => t match {
        case NonTerminal(name, _, _) => op(b, f(name))
        case _ => b
      }
    }
  // check wheter if tokens contain module nonterminal
  def containsModuleNT: Boolean =
    check(Grammar.isModuleNT, false, (x, y) => x || y)
  def containsSupplementalNT: Boolean =
    check(Grammar.isSupplementalNT, false, (x, y) => x || y)
  def isTarget: Boolean =
    check(Grammar.isTargetNT, true, (x, y) => x && y)
  // check if rhs satifies parameters
  def satisfy(params: Set[String]): Boolean = {
    if (cond == "") true
    else {
      if (cond startsWith "p") params contains (cond substring 1)
      else !(params contains (cond substring 2))
    }
  }
}
object Rhs extends RhsParsers {
  def apply(str: String): Rhs = parseAll(rhs, str).get
}
