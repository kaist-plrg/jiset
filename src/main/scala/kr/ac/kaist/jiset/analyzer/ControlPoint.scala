package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._

// control points
trait ControlPoint {
  // view of control points
  val view: View

  // conversion to string
  override def toString: String = this match {
    case NodePoint(node, view) => s"$node:$view"
    case ReturnPoint(func, view) => s"RETURN:$view"
  }
}
case class NodePoint[T <: Node](node: T, view: View) extends ControlPoint
case class ReturnPoint(func: Function, view: View) extends ControlPoint

// view abstraction
class View(val tys: List[Type]) {
  // conversion to string
  override def toString: String =
    if (USE_VIEW) tys.mkString("[", ", ", "]") else "I"

  // equality check
  override def equals(any: Any): Boolean = any match {
    case (that: View) => this.tys == that.tys
    case _ => false
  }

  // hash code
  override def hashCode: Int = tys.hashCode
}
object View {
  def apply(seq: Type*): View =
    if (USE_VIEW) new View(seq.toList) else new View(Nil)
  def apply(tys: List[Type]): View =
    if (USE_VIEW) new View(tys) else new View(Nil)
}
