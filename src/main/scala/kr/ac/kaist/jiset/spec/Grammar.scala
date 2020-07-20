package kr.ac.kaist.jiset.spec

import spray.json._

// ECMAScript grammars
case class Grammar(
    var lexProds: List[Production],
    var prods: List[Production]
) {
  def getProdByName(name: String): Production =
    prods.find(_.lhs.name == name) match {
      case Some(prod) => prod
      case None => throw new Exception(s"Grammar: $name is not production")
    }
}

// ECMAScript grammar helper
object GrammarHelper {
  private val moduleNT: Set[String] = Set(
    "ExportDeclaration",
    "ExportFromClause",
    "ExportsList",
    "ExportsSpecifier",
    "FromClause",
    "ImportCall",
    "ImportClause",
    "ImportDeclaration",
    "ImportMeta",
    "ImportSpecifier",
    "ImportedBinding",
    "ImportedDefaultBinding",
    "ImportsList",
    "Module",
    "ModuleBody",
    "ModuleItem",
    "ModuleItemList",
    "ModuleSpecifier",
    "NameSpaceImport",
    "NamedExports",
    "NamedImports",
    "RegularExpressionLiteral",
  )
  private val supplementalNT: Set[String] = Set(
    "CallMemberExpression",
    "ParenthesizedExpression",
    "ArrowFormalParameters",
    "AsyncArrowHead",
    "AssignmentPattern",
    "ObjectAssignmentPattern",
    "ArrayAssignmentPattern",
    "AssignmentRestProperty",
    "AssignmentPropertyList",
    "AssignmentElementList",
    "AssignmentElisionElement",
    "AssignmentProperty",
    "AssignmentElement",
    "AssignmentRestElement",
    "DestructuringAssignmentTarget"
  )
  def isModuleNT(name: String): Boolean = moduleNT contains name
  def isSupplementalNT(name: String): Boolean = supplementalNT contains name
  def isTargetNT(name: String): Boolean = !(isModuleNT(name) || isSupplementalNT(name))
}

// productions
case class Production(
    var lhs: Lhs,
    var rhsList: List[Rhs]
)

// left-hand-sides
case class Lhs(
    var name: String,
    var params: List[String]
) {
  def isModule: Boolean = GrammarHelper.isModuleNT(name)
  def isSupplemental: Boolean = GrammarHelper.isSupplementalNT(name)
  def isTarget: Boolean = GrammarHelper.isTargetNT(name)
  def isScript: Boolean = name == "Script"
}

// right-hand-sides
case class Rhs(
    var tokens: List[Token],
    var cond: String
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
    check(GrammarHelper.isModuleNT, false, (x, y) => x || y)
  def containsSupplementalNT: Boolean =
    check(GrammarHelper.isSupplementalNT, false, (x, y) => x || y)
  def isTarget: Boolean =
    check(GrammarHelper.isTargetNT, true, (x, y) => x && y)
  // check if rhs satifies parameters
  def satisfy(params: Set[String]): Boolean = {
    if (cond == "") true
    else {
      if (cond startsWith "p") params contains (cond substring 1)
      else !(params contains (cond substring 2))
    }
  }
}

// tokens
trait Token {
  def norm: Option[Token] = this match {
    case ButNot(base, _) => base.norm
    case EmptyToken | Lookahead(_, _) => None
    case t => Some(t)
  }
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
