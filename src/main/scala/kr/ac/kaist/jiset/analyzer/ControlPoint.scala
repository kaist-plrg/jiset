package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._
import domain._

// control points
trait ControlPoint {
  // view of control points
  val view: View

  // conversion to string
  override def toString: String = this match {
    case NodePoint(node, view) => s"$node:$view"
    case ReturnPoint(func, view) => s"${func.name}:$view"
  }
}
case class NodePoint[T <: Node](node: T, view: View) extends ControlPoint
case class ReturnPoint(func: Function, view: View) extends ControlPoint

// view abstraction
case class View(tys: List[Type]) {
  // conversion to string
  override def toString: String = tys.mkString("[", ", ", "]")
}
object View {
  def apply(seq: Type*): View = View(seq.toList)
}
