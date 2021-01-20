package kr.ac.kaist.jiset.spec.grammar

// ECMAScript grammar right-hand-sides
case class Rhs(
    tokens: List[Token],
    condOpt: Option[RhsCond]
) {
  // rhs name
  def names: List[String] = tokens.foldLeft(List[String]("")) {
    case (names, Terminal(term)) => names.map(_ + term)
    case (names, NonTerminal(name, _, optional)) => names.flatMap(x => {
      if (optional) List(x, x + name) else List(x + name)
    })
    case (names, ButNot(NonTerminal(base, _, _), cases)) =>
      val butnot = cases.flatMap(_ match {
        case NonTerminal(name, _, _) => Some(name)
        case _ => None
      }).mkString
      names.map(_ + s"${base}butnot$butnot")
    case (names, _) => names
  }

  // get non-terminals
  def getNTs: List[NonTerminal] = tokens.flatMap(_.getNT)

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
  def satisfy(params: Set[String]): Boolean = condOpt.fold(true)(_ match {
    case RhsCond(name, pass) => (params contains name) == pass
  })

  // conversion to string
  override def toString: String = {
    val condStr = condOpt.fold("") {
      case RhsCond(name, true) => s"[+$name] "
      case RhsCond(name, false) => s"[~$name] "
    }
    val tokensStr = tokens.mkString(" ")
    s"$condStr$tokensStr"
  }
}

case class RhsCond(name: String, pass: Boolean) {
  // conversion to string
  override def toString: String = s"${if (pass) "" else "!"}p$name"
}
