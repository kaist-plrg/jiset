package kr.ac.kaist.jiset.spec

import spray.json._

// ECMAScript grammars
case class Grammar(
    var lexProds: List[Production],
    var prods: List[Production]
)

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
    "Script",
    "ScriptBody",
  )
  def isModuleNT(name: String): Boolean = moduleNT contains name
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
  def isModuleNT: Boolean = GrammarHelper.isModuleNT(name)
}

// right-hand-sides
case class Rhs(
    var tokens: List[Token],
    var cond: String
) {
  // check whehter if tokens is a single nonterminal
  def isSingleNT: Boolean = tokens match {
    case List(_: NonTerminal) => true
    case _ => false
  }
  def isModuleNT: Boolean = tokens.foldLeft(false) {
    case (b, t) => t match {
      case NonTerminal(name, _, _) => b || GrammarHelper.isModuleNT(name)
      case _ => b
    }
  }
}

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
