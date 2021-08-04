package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.cfg._

// control points
trait ControlPoint extends CheckerElem {
  // view of control points
  val view: View
}
case class NodePoint[+T <: Node](node: T, view: View) extends ControlPoint
case class ReturnPoint(func: Function, view: View) extends ControlPoint
