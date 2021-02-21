package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.cfg._
import domain._

// abstract transfer function
class AbsTransfer(sem: AbsSemantics) {
  // CFG
  val cfg = sem.cfg

  // transfer function for control points
  def apply(cp: ControlPoint): Unit = cp match {
    case (np: NodePoint) => this(np)
    case (rp: ReturnPoint) => this(rp)
  }

  // transfer function for node points
  def apply(np: NodePoint): Unit = {
    val st = sem(np)
    val NodePoint(node, view) = np
    val helper = new Helper(ReturnPoint(node.func, view))
    import helper._
    node match {
      case (entry: Entry) =>
        for (n <- cfg.next(entry)) sem += NodePoint(n, view) -> st
      case (exit: Exit) => // TODO detect missing return
      case (block: Block) =>
        val nextSt = block.insts.foldLeft(st)(transfer)
        for (n <- cfg.next(block)) sem += NodePoint(n, view) -> nextSt
      case Call(inst) => ???
      case branch @ Branch(expr) => ???
    }
  }

  // transfer function for return points
  def apply(rp: ReturnPoint): Unit = {
    // TODO handle inter-procedural cases
  }

  private class Helper(ret: ReturnPoint) {
    // transfer function for instructions
    def transfer(st: AbsState, inst: NormalInst): AbsState = inst match {
      case IExpr(expr) => ???
      case ILet(id, expr) => ???
      case IAssign(ref, expr) => ???
      case IDelete(ref) => ???
      case IAppend(expr, list) => ???
      case IPrepend(expr, list) => ???
      case IReturn(expr) =>
        val (s0, v) = transfer(st, expr)
        sem.doReturn(ret -> (s0.heap, v))
        AbsState.Bot
      case IThrow(id) => ???
      case IAssert(expr) => ???
      case IPrint(expr) => ???
      case IWithCont(id, params, bodyInst) => ???
      case ISetType(expr, ty) => ???
    }

    // transfer function for expressions
    def transfer(st: AbsState, expr: Expr): (AbsState, AbsValue) = expr match {
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
}
