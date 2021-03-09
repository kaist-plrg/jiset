package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir
import scala.collection.mutable.Queue

// Walker for CFG
trait UnitWalker extends ir.UnitWalker {
  val cfg: CFG
  import cfg._

  // breadth-first search (BFS) walker for functions
  def walk(func: Function): Unit = {
    var visited: Set[Node] = Set()
    var queue: Queue[Node] = Queue(func.entry)
    def add(node: Node): Unit =
      if (!(visited contains node)) queue.enqueue(node)

    while (!queue.isEmpty) {
      val node = queue.dequeue
      walk(node)
      visited += node
      nexts(node).foreach(add)
    }
  }

  // walker for nodes
  def walk(node: Node): Unit = node match {
    case Exit(_) =>
    case Entry(_) =>
    case Call(_, inst) => walk(inst)
    case Branch(_, cond) => walk(cond)
    case Block(_, insts) => walkList[ir.Inst](insts, walk)
  }
}
