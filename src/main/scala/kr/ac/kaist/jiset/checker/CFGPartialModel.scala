package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.cfg._

// PartialModel
case class CFGPartialModel(partialMap: Map[Function, Map[View, PartialFunc]]) {
  // getter
  def get(func: Function, view: View): Option[PartialFunc] =
    partialMap
      .getOrElse(func, Map())
      .get(view)
}
object CFGPartialModel {
  def apply(recorder: VisitRecorder): CFGPartialModel = CFGPartialModel(recorder.visitMap.map {
    case (func, viewMap) => func -> (viewMap.map {
      case (view, nodeMap) => view -> PartialFunc(func, view, nodeMap.keys.toSet)
    }).toMap
  }.toMap)
}

// PartialFunc for each function and view
case class PartialFunc(func: Function, view: View, reachables: Set[Node]) extends CFGElem {
  import PartialFunc._

  // Get the information of given branch
  lazy val reachable: Map[Branch, Reachable] = ???

  // Get the shortcut
  lazy val shortcut: Map[Linear, Node] = ???

  // order between partial model
  def ⊑(that: PartialFunc): Boolean = this.reachables subsetOf that.reachables
}
object PartialFunc {
  type Reachable = Reachable.Value
  object Reachable extends Enumeration {
    val Then, Else, Both = Value
  }
}
