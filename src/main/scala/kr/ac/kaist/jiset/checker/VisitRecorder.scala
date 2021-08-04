package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.cfg._
import scala.collection.mutable.{ Map => MMap }

// recorder for visited CFG nodes
case class VisitRecorder(visitMap: VisitRecorder.FuncMap) extends CheckerElem {
  def record(func: Function, view: View, node: Node, fnameOpt: Option[String]): Unit = {
    var viewMap = visitMap.getOrElse(func, MMap())
    var nodeMap = viewMap.getOrElse(view, MMap())
    if (nodeMap.getOrElse(node, None) == None) {
      nodeMap += node -> fnameOpt
      viewMap += view -> nodeMap
      visitMap += func -> viewMap
    }
  }
}
object VisitRecorder {
  type NodeMap = MMap[Node, Option[String]]
  type ViewMap = MMap[View, NodeMap]
  type FuncMap = MMap[Function, ViewMap]

  def apply(pairs: (Function, ViewMap)*): VisitRecorder = VisitRecorder(MMap() ++ pairs.map {
    case (f, v) => f -> v
  }.toMap[Function, ViewMap])
}

