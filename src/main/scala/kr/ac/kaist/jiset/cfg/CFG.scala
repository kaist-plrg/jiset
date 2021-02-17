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

  // getters for forward edges
  def next(node: Node): Set[(Edge, Node)] = forwards.getOrElse(node, Set())
  def nextNode(branch: Branch, cond: Boolean): Node =
    next(branch).find(_._1 == CondEdge(cond)).get._2
  def nextNodes(node: Node): Set[Node] = next(node).map(_._2)

  // getters for backward edges
  def prev(node: Node): Set[(Edge, Node)] = backwards.getOrElse(node, Set())
  def prevNodes(node: Node): Set[Node] = prev(node).map(_._2)
}
object CFG {
  def apply(spec: ECMAScript): CFG = {
    val cfg = new CFG
    spec.algos.foreach(cfg += Translator(_))
    cfg
  }
}
