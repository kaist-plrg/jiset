package kr.ac.kaist.jiset.analyzer

case class TyInfo(name: String, parent: Option[String], props: Map[String, AbsType])
object TyInfo {
  def apply(name: String, pairs: (String, AbsType)*): TyInfo = TyInfo(name, None, pairs.toMap)
  def apply(name: String, parent: String, pairs: (String, AbsType)*): TyInfo =
    TyInfo(name, Some(parent), pairs.toMap)
}
