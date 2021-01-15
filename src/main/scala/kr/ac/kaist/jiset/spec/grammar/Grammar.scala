package kr.ac.kaist.jiset.spec.grammar

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.Useful._

// ECMAScript grammars
case class Grammar(
    lexProds: List[Production],
    prods: List[Production]
) {
  val idxMap: Map[String, (Int, Int)] = (for {
    prod <- lexProds ++ prods
    pair <- prod.getIdxMap
  } yield pair).toMap

  def getProdByName(name: String): Production =
    prods.find(_.lhs.name == name) match {
      case Some(prod) => prod
      case None => throw new Exception(s"Grammar: $name is not production")
    }

  private def getSorted(prods: List[Production]) = prods.sortBy(_.lhs.name)
  lazy val sortedProds = (getSorted(lexProds), getSorted(prods))

  // conversion to string
  override def toString: String = {
    val (lprods, sprods) = sortedProds
    Grammar.lexicalHeader + LINE_SEP +
      lprods.mkString(LINE_SEP * 2) + LINE_SEP +
      Grammar.syntacticHeader + LINE_SEP +
      sprods.mkString(LINE_SEP * 2)
  }
}

object Grammar {
  val lexicalHeader = "[Lexical Productions]"
  val syntacticHeader = "[Syntactic Productions]"

  def apply(src: String): Grammar = {
    def getProds(lines: List[String]) = (for {
      prodStr <- splitBy(lines, "")
      prod <- optional(Production(prodStr))
    } yield prod).toList

    val lines = src.split(LINE_SEP).toList
    val synIdx = lines.indexOf(syntacticHeader)
    val (lexLines, synLines) = lines.splitAt(synIdx) match {
      case (l0, l1) => (l0.tail, l1.tail)
    }
    val lexProds = getProds(lexLines)
    val synProds = getProds(synLines)
    Grammar(lexProds, synProds)
  }

  // check target non-terminals
  def isTargetNT(name: String): Boolean = !(isModuleNT(name) || isSupplementalNT(name))

  // check module non-terminals
  def isModuleNT(name: String): Boolean = moduleNT contains name
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

  // check supplemental non-terminals
  def isSupplementalNT(name: String): Boolean = supplementalNT contains name
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
}
