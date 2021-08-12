package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset._
import kr.ac.kaist.jiset.cfg._
import scala.collection.mutable.{ Map => MMap }

// recorder for visited CFG nodes
case class VisitRecorder(
  funcMap: VisitRecorder.FuncMap
) extends CheckerElem {
  import VisitRecorder._

  // record visited nodes
  def record(
    func: Function,
    view: View,
    node: Node,
    fnameOpt: Option[String]
  ): Unit = {
    val given = fnameOpt.getOrElse("UNKNOWN")
    val viewMap = funcMap.getOrElseUpdate(func, MMap())
    val nodeMap = viewMap.getOrElseUpdate(view, MMap())
    val elem = nodeMap.getOrElseUpdate(node, Elem(0, given))
    elem.count = elem.count + 1
  }

  // number of components
  def func: Long = funcMap.size
  def view: Long = funcMap.map(_._2.size).sum
  def node: Long = funcMap.map(_._2.map(_._2.size).sum).sum

  // counts by components
  def funcCount: FuncCount = viewCount.map { case (f, m) => f -> m.values.sum }
  def viewCount: ViewCount = funcMap.toMap.map {
    case (f, m) => f -> m.toMap.map {
      case (v, m) => v -> m.collect {
        case (_: Entry, elem) => elem.count
      }.sum
    }
  }
}
object VisitRecorder {
  // internal types
  case class Elem(var count: Long, var fname: String)

  type FuncMap = MMap[Function, ViewMap]
  type ViewMap = MMap[View, NodeMap]
  type NodeMap = MMap[Node, Elem]

  type FuncCount = Map[Function, Long]
  type ViewCount = Map[Function, Map[View, Long]]

  // constructors
  def apply(pairs: (Function, ViewMap)*): VisitRecorder =
    VisitRecorder(MMap.from(pairs))
}
