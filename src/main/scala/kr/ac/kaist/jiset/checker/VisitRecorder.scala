package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.cfg._
import scala.collection.mutable.{ Map => MMap }

// recorder for visited CFG nodes
case class VisitRecorder(
  visitMap: VisitRecorder.FuncMap
) extends CheckerElem {
  // record visited nodes
  def record(
    func: Function,
    view: View,
    node: Node,
    fnameOpt: Option[String]
  ): Unit = {
    val fname = fnameOpt.getOrElse("UNKNOWN")
    visitMap
      .getOrElseUpdate(func, { _func += 1; MMap() })
      .getOrElseUpdate(view, { _view += 1; MMap() })
      .getOrElseUpdate(node, { _node += 1; fname })
  }

  // visited functions
  private var _func = 0
  private var _view = 0
  private var _node = 0
  def func: Int = _func
  def view: Int = _view
  def node: Int = _node
}
object VisitRecorder {
  // internal types
  type NodeMap = MMap[Node, String]
  type ViewMap = MMap[View, NodeMap]
  type FuncMap = MMap[Function, ViewMap]

  // constructors
  def apply(pairs: (Function, ViewMap)*): VisitRecorder =
    VisitRecorder(MMap.from(pairs))
}

