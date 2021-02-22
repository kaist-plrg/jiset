package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.util.UId

// control flow graph
class CFG extends UId {
  private var funcs: Set[Function] = Set()
  private var nodes: Set[Node] = Set()

  // insert new functions
  def +=(func: Function): Unit = {
    funcs += func
    nodes ++= func.nodes
  }

  // getters for functions and nodes
  def allFunctions: Set[Function] = funcs
  def allNodes: Set[Node] = nodes
}
object CFG {
  def apply(spec: ECMAScript): CFG = {
    val cfg = new CFG
    spec.targetAlgos.foreach(cfg += Translator(_))
    cfg
  }
}
