package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.cfg._
import domain._

// abstract transfer function
class AbsTransfer(
  cfg: CFG,
  sem: AbsSemantics
) {
  // transfer function for control points
  def apply(st: AbsState, cp: ControlPoint): List[Result[ControlPoint]] = {
    val ControlPoint(node, view) = cp
    val prev = Result(node, st)
    apply(prev).flatMap {
      case next => view.next(prev, next).map {
        case result => Result(ControlPoint(next.elem, result.elem), result.st)
      }
    }
  }

  // transfer function for nodes
  def apply(result: Result[Node]): List[Result[Node]] = apply(result.st, result.elem)
  def apply(st: AbsState, node: Node): List[Result[Node]] = node match {
    case Entry() => cfg.nextNodes(node).toList.map(Result(_, st))
    case Exit() => ???
    case Block(insts) =>
      val nextSt = insts.foldLeft(st)(apply)
      cfg.nextNodes(node).toList.map(Result(_, nextSt))
    case Call(inst) => ???
    case branch @ Branch(expr) => ???
  }

  // transfer function for instructions
  def apply(st: AbsState, inst: NormalInst): AbsState = inst match {
    case IExpr(expr) => ???
    case ILet(id, expr) => ???
    case IAssign(ref, expr) => ???
    case IDelete(ref) => ???
    case IAppend(expr, list) => ???
    case IPrepend(expr, list) => ???
    case IReturn(expr) =>
      val (s0, v) = apply(st, expr)
      AbsState.Bot
    case IThrow(id) => ???
    case IAssert(expr) => ???
    case IPrint(expr) => ???
    case IWithCont(id, params, bodyInst) => ???
    case ISetType(expr, ty) => ???
  }

  // transfer function for expressions
  def apply(st: AbsState, expr: Expr): (AbsState, AbsValue) = expr match {
    case ENum(n) => (st, AbsNum(n))
    case EINum(n) => (st, AbsINum(n))
    case EBigINum(b) => (st, AbsBigINum(b))
    case EStr(str) => (st, AbsStr(str))
    case EBool(b) => (st, AbsBool(b))
    case EUndef => (st, AbsUndef.Top)
    case ENull => (st, AbsNull.Top)
    case EAbsent => (st, AbsAbsent.Top)
    case EMap(ty, props) => ???
    case EList(exprs) => ???
    case ESymbol(desc) => ???
    case EPop(list, idx) => ???
    case ERef(ref) => ???
    case ECont(params, body) => ???
    case EUOp(uop, expr) => ???
    case EBOp(bop, left, right) => ???
    case ETypeOf(expr) => ???
    case EIsCompletion(expr) => ???
    case EIsInstanceOf(base, name) => ???
    case EGetElems(base, name) => ???
    case EGetSyntax(base) => ???
    case EParseSyntax(code, rule, flags) => ???
    case EConvert(source, target, flags) => ???
    case EContains(list, elem) => ???
    case EReturnIfAbrupt(expr, check) => ???
    case ECopy(obj) => ???
    case EKeys(mobj) => ???
    case ENotSupported(msg) => ???
  }

  // pruning abstract states using conditions
  def prune(st: AbsState, expr: Expr, cond: Boolean): AbsState = ???
}
