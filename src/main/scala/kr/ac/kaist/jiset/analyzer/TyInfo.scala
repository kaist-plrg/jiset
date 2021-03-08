package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._

case class TyInfo(name: String, parent: Option[String], props: Map[String, AbsValue])
object TyInfo {
  def apply(name: String, pairs: (String, AbsValue)*): TyInfo = TyInfo(name, None, pairs.toMap)
  def apply(name: String, parent: String, pairs: (String, AbsValue)*): TyInfo =
    TyInfo(name, Some(parent), pairs.toMap)
}
