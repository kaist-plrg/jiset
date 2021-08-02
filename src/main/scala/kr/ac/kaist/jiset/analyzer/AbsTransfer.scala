package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.js._
import scala.annotation.tailrec

// abstract transfer function
case class AbsTransfer(sem: AbsSemantics) {
  // transfer function for control points
  def apply(cp: ControlPoint): Unit = cp match {
    case (np: NodePoint[_]) => this(np)
    case (rp: ReturnPoint) => this(rp)
  }

  // transfer function for node points
  def apply[T <: Node](np: NodePoint[T]): Unit = {
    val st = sem(np)
    val NodePoint(node, view) = np
    val func = cfg.funcOf(node)
    val ret = ReturnPoint(func, view)
    node match {
      case (entry: Entry) =>
        sem += NodePoint(cfg.nextOf(entry), view) -> st
      case (exit: Exit) => ???
      case (normal: Normal) => ???
      case (call: Call) => ???
      case arrow @ Arrow(_, inst, fid) => ???
      case branch @ Branch(_, inst) => ???
    }
  }

  // transfer function for return points
  def apply(rp: ReturnPoint): Unit = {
    val newT = sem(rp)
    for (np @ NodePoint(call, view) <- sem.getRetEdges(rp)) {
      val x = call.inst.id
      val nextNP = np.copy(node = cfg.nextOf(call))
      ???
    }
  }
}
