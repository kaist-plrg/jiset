package kr.ac.kaist.jiset.checker

import kr.ac.kaist.jiset.cfg.{ DotPrinter => _, _ }
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.Queue
import scala.annotation.tailrec

// CFG PartialModel
case class CFGPartialModel(
  partialMap: Map[Function, Map[View, PartialFunc]]
) extends CheckerElem {
  // getter
  def get(func: Function, viewOpt: Option[View]): Option[PartialFunc] =
    viewOpt.map(view => partialMap.getOrElse(func, Map()).get(view)).flatten

  val partialSpec: Map[Function, Map[View, Int]] = partialMap.map {
    case (func, viewMap) => func -> viewMap.map {
      case (view, pfunc) => view -> Node.getLineCount(pfunc.nodes)
    }
  }
  val originalSpec: Map[Function, Int] = partialMap.map {
    case (func, _) => func -> Node.getLineCount(func.nodes)
  }
}
object CFGPartialModel {
  def apply(recorder: VisitRecorder): CFGPartialModel = CFGPartialModel(
    recorder.funcMap.map {
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
  private def getReachable(
    func: Function,
    branch: Branch,
    t: Node,
    e: Node
  ): Reachable = (reachables contains t, reachables contains e) match {
    case (true, true) => Reachable.Both
    case (true, false) => Reachable.Then
    case (false, true) => Reachable.Else
    case (false, false) =>
      error(s"Should be reachable branch: $branch @ $func")
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
  } yield branch -> getReachable(func, branch, t, e)).toMap

  // shortcut of linear node
  lazy val shortcut: Map[Linear, Node] = (for {
    linear <- reachables.collect { case l: Linear => l }
    nextNode <- func.nexts.get(linear)
    dest <- nextNode match {
      case b: Branch if reachables(b) =>
        reachable(b) match {
          case Reachable.Both => None
          case _ => Some(jump(b))
        }
      case _ => None
    }
  } yield linear -> dest).toMap

  // get nodes of partial function
  def nodes: Set[Node] = {
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

  // get spec from partial function's node
  def spec: Iterable[String] = func.algoOption match {
    case None => Iterable[String]()
    case Some(algo) =>
      val code = algo.code
      val lines = Node.getLines(nodes)
      code.zipWithIndex.flatMap {
        case (step, line) if lines contains line => Some(step)
        case _ => None
      }
  }

  // order between partial model
  def ⊑(that: PartialFunc): Boolean =
    this.func == that.func &&
      this.view == that.view &&
      (this.reachables subsetOf that.reachables)

  // conversion to DOT
  lazy val toDot: String = (new DotPrinter {
    def getId(func: Function): String = s"cluster${func.uid}_${norm(view)}"
    def getId(node: Node): String = s"node${node.uid}_${norm(view)}"
    def getName(func: Function): String = {
      val viewName = view.toString.replaceAll("\"", "\\\\\"")
      s"${func.name}:$viewName"
    }
    def getColor(node: Node): String = {
      if (reachables contains node) REACH
      else NON_REACH
    }
    def getColor(from: Node, to: Node): String = {
      if (Set(from, to) subsetOf reachables) REACH
      else NON_REACH
    }
    def getBgColor(node: Node): String = {
      if (nodes contains node) SELECTED
      else NORMAL
    }
    def apply(app: Appender): Unit = {
      addFunc(func, app)
      for ((f, t) <- shortcut) addShortcut(f, t, app)
    }
    def addShortcut(
      from: Node,
      to: Node,
      app: Appender
    ): Unit = {
      val fid = getId(from)
      val tid = getId(to)
      addEdge(fid, tid, SHORTCUT, Some("shortcut"), app)
    }
  }).toString
}
object PartialFunc {
  type Reachable = Reachable.Value
  object Reachable extends Enumeration {
    val Then, Else, Both = Value
  }
}
