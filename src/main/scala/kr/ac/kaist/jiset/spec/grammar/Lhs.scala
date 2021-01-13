package kr.ac.kaist.jiset.spec

// ECMAScript grammar left-hand-sides
case class Lhs(
    var name: String,
    var params: List[String]
) {
  def isModule: Boolean = Grammar.isModuleNT(name)
  def isSupplemental: Boolean = Grammar.isSupplementalNT(name)
  def isTarget: Boolean = Grammar.isTargetNT(name)
  def isScript: Boolean = name == "Script"
}
object Lhs extends LhsParsers {
  def apply(str: String): Lhs = parse(lhs, str).get
}
