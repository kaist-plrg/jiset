package kr.ac.kaist.jiset.spec.grammar

import kr.ac.kaist.jiset.spec.grammar.token._
import kr.ac.kaist.jiset.spec.SpecComponent

// ECMAScript grammar right-hand-sides
case class Rhs(
  tokens: List[Token],
  condOpt: Option[RhsCond]
) extends SpecComponent {
  // get rhs name
  def name: String = tokens.foldLeft("") {
    case (prev, Terminal(term)) => prev + term
    case (prev, NonTerminal(name, _, _)) => prev + name
    case (prev, ButNot(NonTerminal(base, _, _), cases)) => prev + base
    case (prev, _) => prev
  }

  // get rhs all names
  def allNames: List[String] = tokens.foldLeft(List[String]("")) {
    case (names, Terminal(term)) => names.map(_ + term)
    case (names, NonTerminal(name, _, optional)) => names.flatMap(x => {
      if (optional) List(x, x + name) else List(x + name)
    })
    case (names, ButNot(NonTerminal(base, _, _), cases)) =>
      names.map(_ + base)
    case (names, _) => names
  }

  // get non-terminals
  def getNTs: List[NonTerminal] = tokens.flatMap(_.getNT)

  // get non-terminals
  def toNTs: List[NonTerminal] = tokens.flatMap(_.norm)

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
  def satisfy(params: Set[String]): Boolean = condOpt.fold(true)(_ match {
    case RhsCond(name, pass) => (params contains name) == pass
  })
}

case class RhsCond(name: String, pass: Boolean) {
  // conversion to parser
  def getParser: String = s"${if (pass) "" else "!"}p$name"
}
