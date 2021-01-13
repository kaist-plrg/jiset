package kr.ac.kaist.jiset.spec

import kr.ac.kaist.jiset.util.Useful._

// ECMAScript grammars
case class Grammar(
    lexProds: List[Production],
    prods: List[Production]
) {
  val idxMap: Map[String, (Int, Int)] = (for {
    prod <- prods
    pair <- prod.getIdxMap
  } yield pair).toMap

  def getProdByName(name: String): Production =
    prods.find(_.lhs.name == name) match {
      case Some(prod) => prod
      case None => throw new Exception(s"Grammar: $name is not production")
    }
}
object Grammar {
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
