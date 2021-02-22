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
        sem += NodePoint(entry.next.get, view) -> st
      case (exit: Exit) => // TODO detect missing return
      case (block: Block) =>
        val nextSt = block.insts.foldLeft(st)(transfer)
        sem += NodePoint(block.next.get, view) -> st
      case Call(inst, next) => ???
      case branch @ Branch(expr, tnext, fnext) =>
        ???
        val (s, v) = transfer(st, expr)
        ???
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
      case EMap(ty, props) =>
        // TODO handling type information
        val (newSt, map) = props.foldLeft((st, Map[String, AbsValue]())) {
          case ((s0, map), (EStr(k), expr)) =>
            val (s1, v) = transfer(s0, expr)
            (s1, map + (k -> v))
          case _ => ??? // TODO handling non-string keys
        }
        newSt.allocMap(map)
      case EList(exprs) =>
        val (newSt, vs) = exprs.foldLeft((st, Vector[AbsValue]())) {
          case ((s0, vs), expr) =>
            val (s1, v) = transfer(s0, expr)
            (s1, vs :+ v)
        }
        newSt.allocList(vs.toList)
      case ESymbol(desc) => desc match {
        case EStr(desc) => st.allocSymbol(desc)
        case _ => ??? // TODO handling non-string descriptions
      }
      case EPop(list, idx) =>
        val (s0, l) = transfer(st, list)
        val (s1, k) = transfer(s0, idx)
        s1.pop(l, k)
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
