package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.ires.ir._
import kr.ac.kaist.jiset.cfg._
import domain._

// abstract transfer function
object AbsTransfer {
  // control point-wise transfer
  def apply(cp: ControlPoint, st: AbsState): List[Result[ControlPoint]] = {
    val ControlPoint(node, view) = cp
    val prev = (node, st)
    apply(prev).flatMap {
      case next @ (nextNode, _) => view.next(prev, next).map {
        case (nextView, nextSt) => (ControlPoint(nextNode, nextView), nextSt)
      }
    }
  }

  // node-wise transfer
  def apply(result: Result[Node]): List[Result[Node]] = apply(result._1, result._2)
  def apply(node: Node, st: AbsState): List[Result[Node]] = node match {
    case Entry() => ???
    case Exit() => ???
    case Block(insts) => ???
    case Call(inst) => ???
    case Branch(cond) => ???
  }

  // instruction-wise transfer
  def apply(inst: NormalInst, st: AbsState): AbsState = inst match {
    case IExpr(expr) => ???
    case ILet(id, expr) => ???
    case IAssign(ref, expr) => ???
    case IDelete(ref) => ???
    case IAppend(expr, list) => ???
    case IPrepend(expr, list) => ???
    case IReturn(expr) => ???
    case IAssert(expr) => ???
    case IPrint(expr) => ???
    case IWithCont(id, params, bodyInst) => ???
    case ISetType(expr, ty) => ???
  }
}
