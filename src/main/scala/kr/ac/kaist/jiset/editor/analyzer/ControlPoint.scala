package kr.ac.kaist.jiset.editor.analyzer

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.js._

// control points
sealed trait ControlPoint extends AnalyzerElem {

  // get function
  def func: Function

  // check whether it is in a built-in algorithm
  def isBuiltin: Boolean = func.origin match {
    case AlgoOrigin(algo) => algo.isBuiltin
    case _ => false
  }
}
case class NodePoint[+T <: Node](node: T) extends ControlPoint {
  // get function
  def func: Function = cfg.funcOf(node)
}
case class ReturnPoint(func: Function) extends ControlPoint
