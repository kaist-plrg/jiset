package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg.{ Component => _, _ }

// control points
trait ControlPoint extends Component {
  // view of control points
  val view: View
}
case class NodePoint[+T <: Node](node: T, view: View) extends ControlPoint
case class ReturnPoint(func: Function, view: View) extends ControlPoint
