package kr.ac.kaist.jiset.cfg

// CFG edges
trait Edge { val from: Node }
case class LinearEdge(from: Linear, next: Node) extends Edge
case class BranchEdge(from: Branch, thenNext: Node, elseNext: Node) extends Edge
