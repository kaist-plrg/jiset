package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.jiset.ir.{ UnitWalker => IRUnitWalker, Inst }
import scala.collection.mutable.Queue

// Walker for CFG
trait UnitWalker extends IRUnitWalker {
  // cfg
  def walk(cfg: CFG): Unit = ???

  // function
  def walk(func: Function): Unit = {
    val Function(_, entry, exit, nodes) = func

    // BFS
    var visited: Set[Node] = Set()
    var queue: Queue[Node] = Queue(entry)
    def add(node: Node): Unit =
      if (!visited.contains(node)) queue.enqueue(node)

    while (!queue.isEmpty) {
      val node = queue.dequeue
      walk(node)
      visited += node
      node.nexts.foreach(add(_))
    }
  }

  // node
  def walk(node: Node): Unit = node match {
    case Exit() =>
    case Entry(_) =>
    case Call(inst, _) => walk(inst)
    case Branch(cond, _, _) => walk(cond)
    case Block(insts, _) => walkList[Inst](insts, walk)
  }
}
