package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.js._

// control points
trait ControlPoint extends AnalyzerElem {
  // view
  val view: View

  // get function
  def getFunc: Function = this match {
    case NodePoint(node, _) => cfg.funcOf(node)
    case ReturnPoint(func, _) => func
  }

  // check whether it is in a built-in algorithm
  def isBuiltin: Boolean = getFunc.origin match {
    case AlgoOrigin(algo) => algo.isBuiltin
    case _ => false
  }
}
case class NodePoint[+T <: Node](node: T, view: View) extends ControlPoint
case class ReturnPoint(func: Function, view: View) extends ControlPoint
