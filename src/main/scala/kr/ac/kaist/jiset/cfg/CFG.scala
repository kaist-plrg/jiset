package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec.ECMAScript
import kr.ac.kaist.jiset.util.UId

// control flow graph
class CFG extends UId {
  private var funcs: Set[Function] = Set()
  private var nodes: Set[Node] = Set()
  private var forwards: Map[Node, Set[(Edge, Node)]] = Map()
  private var backwards: Map[Node, Set[(Edge, Node)]] = Map()

  // insert new functions
  def +=(func: Function): Unit = {
    funcs += func
    nodes ++= func.nodes
    forwards ++= func.forwards
    backwards ++= func.backwards
  }

  // getters for functions and nodes
  def allFunctions: Set[Function] = funcs
  def allNodes: Set[Node] = nodes

  // getters for forward nodes
  def next(node: Node): List[Node] =
    forwards.getOrElse(node, Set()).map(_._2).toList

  // getters for backward nodes
  def prev(node: Node): List[Node] =
    backwards.getOrElse(node, Set()).map(_._2).toList
}
object CFG {
  def apply(spec: ECMAScript): CFG = {
    val cfg = new CFG
    spec.targetAlgos.foreach(cfg += Translator(_))
    cfg
  }
}
