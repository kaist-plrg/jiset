package kr.ac.kaist.jiset.spec.grammar

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.util.Useful._

// ECMAScript grammars
case class Grammar(
  lexProds: List[Production],
  prods: List[Production]
) {
  // all productions
  val allProds = lexProds ++ prods

  // name mapping
  val nameMap: Map[String, Production] = (for {
    prod <- allProds
  } yield prod.name -> prod).toMap

  // sub-AST mapping
  val subs: Map[String, Set[String]] = (prods.map {
    case Production(lhs, rhsList) =>
      val name = lhs.name
      val subs = rhsList.collect {
        case Rhs(List(NonTerminal(name, _, false)), _) => name
      }.toSet
      name -> subs
  }).toMap

  // recursive sub-AST mapping
  val recSubs: Map[String, Set[String]] = {
    var descs = Map[String, Set[String]]()
    def aux(name: String): Set[String] = descs.get(name) match {
      case Some(set) => set
      case None =>
        val set = (for {
          sub <- subs.getOrElse(name, Set())
          elem <- aux(sub)
        } yield elem) + name
        descs += name -> set
        set
    }
    prods.foreach(prod => aux(prod.name))
    descs
  }

  // index mapping
  val idxMap: Map[String, (Int, Int)] = (for {
    prod <- allProds
    pair <- prod.getIdxMap
  } yield pair).toMap

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
