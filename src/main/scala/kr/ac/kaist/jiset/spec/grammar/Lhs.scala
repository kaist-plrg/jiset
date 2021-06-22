package kr.ac.kaist.jiset.spec.grammar

import kr.ac.kaist.jiset.spec.SpecComponent

// ECMAScript grammar left-hand-sides
case class Lhs(
  name: String,
  params: List[String]
) extends SpecComponent {
  def isModule: Boolean = Grammar.isModuleNT(name)
  def isSupplemental: Boolean = Grammar.isSupplementalNT(name)
  def isTarget: Boolean = Grammar.isTargetNT(name)
  def isScript: Boolean = name == "Script"
}
