package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.LINE_SEP
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.analyzer.domain.JsonProtocol._
import spray.json._

class AbsSemantics {
  // internal view-wise abstract states
  private var map: Map[View, AbsState] = Map()

  // lookup
  def apply(view: View): AbsState = map.getOrElse(view, AbsState.Bot)

  // update view-wise abstract states
  def +=(pair: (View, AbsState)): Unit = map += pair

  // return edges
  private var returnMap: Map[View, List[View]] = Map()

  // lookup return edges
  def getRetEdges(view: View): List[View] = returnMap.getOrElse(view, Nil)

  // update return edges
  def /=(pair: (View, List[View])): Unit = returnMap += pair

  // conversion to string
  override def toString: String = map.map {
    case (view, st) => s"$view -> ${st.toJson.prettyPrint}"
  }.mkString(LINE_SEP)
}
