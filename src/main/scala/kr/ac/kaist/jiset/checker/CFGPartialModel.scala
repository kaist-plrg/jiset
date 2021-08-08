package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.Queue
import scala.annotation.tailrec

// CFG PartialModel
case class CFGPartialModel(
  partialMap: Map[Function, Map[View, PartialFunc]]
) extends CheckerElem {
  // getter
  def get(func: Function, view: View): Option[PartialFunc] =
    partialMap
      .getOrElse(func, Map())
      .get(view)
}
object CFGPartialModel {
  def apply(recorder: VisitRecorder): CFGPartialModel = CFGPartialModel(
    recorder.visitMap.map {
      case (func, viewMap) => func -> (viewMap.map {
        case (view, nodeMap) => view -> PartialFunc(func, view, nodeMap.keys.toSet)
      }).toMap
    }.toMap
  )
}

// PartialFunc for each function and view
case class PartialFunc(
  func: Function,
  view: View,
  reachables: Set[Node]
) extends CheckerElem {
  import PartialFunc._

  // get reachable status
  private def getReachable(t: Node, e: Node): Reachable =
    (reachables contains t, reachables contains e) match {
      case (true, true) => Reachable.Both
      case (true, false) => Reachable.Then
      case (false, true) => Reachable.Else
      case (false, false) => error(s"Should be reachable branch")
    }

  // jump branch node
  @tailrec
  private def jump(branch: Branch): Node = {
    val (t, e) = func.branches(branch)
    reachable(branch) match {
      case Reachable.Both => branch
      case Reachable.Then => t match {
        case b: Branch => jump(b)
        case _ => t
      }
      case Reachable.Else => e match {
        case b: Branch => jump(b)
        case _ => e
      }
    }
  }

  // get the information of given branch
  lazy val reachable: Map[Branch, Reachable] = (for {
    branch <- reachables.collect { case b: Branch => b }
    (t, e) <- func.branches.get(branch)
  } yield branch -> getReachable(t, e)).toMap

  // shortcut of linear node
  lazy val shortcut: Map[Linear, Node] = (for {
    linear <- reachables.collect { case l: Linear => l }
    nextNode <- func.nexts.get(linear)
    dest <- nextNode match {
      case b: Branch => reachable(b) match {
        case Reachable.Both => None
        case _ => Some(jump(b))
      }
      case _ => None
    }
  } yield linear -> dest).toMap

  // get nodes of partial function
  lazy val nodes: Set[Node] = {
    // bfs traversal of function using shortcut
    var visited: Set[Node] = Set()
    val queue = Queue[Node](func.entry)
    while (queue.size != 0) {
      val node = queue.dequeue;
      if (!visited.contains(node)) {
        visited += node
        node match {
          case linear: Linear =>
            val nextNode = shortcut.getOrElse(linear, func.nexts(linear))
            queue.enqueue(nextNode)
          case branch: Branch =>
            val (thenNode, elseNode) = func.branches(branch)
            queue.enqueue(thenNode, elseNode)
          case _ =>
        }
      }
    }
    visited
  }

  // order between partial model
  def ⊑(that: PartialFunc): Boolean =
    this.func == that.func &&
      this.view == that.view &&
      (this.reachables subsetOf that.reachables)
}
object PartialFunc {
  type Reachable = Reachable.Value
  object Reachable extends Enumeration {
    val Then, Else, Both = Value
  }
}
