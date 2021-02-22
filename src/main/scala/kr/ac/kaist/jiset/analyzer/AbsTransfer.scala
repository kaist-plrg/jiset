package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.cfg._
import domain._

// abstract transfer function
class AbsTransfer(sem: AbsSemantics) {
  import sem.cfg._

  // transfer function for control points
  def apply(cp: ControlPoint): Unit = cp match {
    case (np: NodePoint) => this(np)
    case (rp: ReturnPoint) => this(rp)
  }

  // transfer function for node points
  def apply(np: NodePoint): Unit = {
    val st = sem(np)
    val NodePoint(node, view) = np
    val helper = new Helper(ReturnPoint(funcOf(node), view))
    import helper._
    node match {
      case (entry: Entry) =>
        sem += NodePoint(next(entry), view) -> st
      case (exit: Exit) => // TODO detect missing return
      case (block: Block) =>
        val nextSt = block.insts.foldLeft(st)(transfer)
        sem += NodePoint(next(block), view) -> nextSt
      case Call(inst) => ???
      case branch @ Branch(expr) =>
        val (s, v) = transfer(st, expr)
        sem += NodePoint(thenNext(branch), view) -> prune(st, expr, true)
        sem += NodePoint(elseNext(branch), view) -> prune(st, expr, false)
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
    // TODO consider the completion records
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
      case ERef(ref) =>
        val (s0, refVal) = transfer(st, ref)
        (s0, s0(refVal))
      case ECont(params, body) => ???
      case EUOp(uop, expr) =>
        val (s0, v) = transfer(st, expr)
        (s0, transfer(uop)(v))
      case EBOp(bop, left, right) =>
        val (s0, l) = transfer(st, left)
        val (s1, r) = transfer(st, right)
        (s1, transfer(bop)(l, r))
      case ETypeOf(expr) =>
        val (s0, v) = transfer(st, expr)
        (s0, s0.typeOf(v))
      case EIsCompletion(expr) =>
        val (s0, v) = transfer(st, expr)
        ??? // TODO after discussing the completion structures
      case EIsInstanceOf(base, name) =>
        val (s0, v) = transfer(st, expr)
        ??? // TODO need discussion
      case EGetElems(base, name) =>
        val (s0, v) = transfer(st, expr)
        ??? // TODO need discussion
      case EGetSyntax(base) => (st, AbsStr.Top) // TODO handling non-AST values
      case EParseSyntax(code, rule, flags) =>
        val (s0, c) = transfer(st, code)
        val (s1, r) = transfer(s0, rule)
        // XXX maybe flags are not necessary in abstract semantics
        ???
      case EConvert(source, target, flags) =>
        val (s0, v) = transfer(st, source)
        ??? // TODO need discussion
      case EContains(list, elem) =>
        val (s0, l) = transfer(st, list)
        val (s1, e) = transfer(s0, elem)
        (s1, s1.contains(l, e))
      case EReturnIfAbrupt(expr, check) =>
        transfer(st, expr) // TODO support abrupt completion check
      case ECopy(obj) =>
        val (s0, v) = transfer(st, obj)
        s0.copyOf(v)
      case EKeys(obj) =>
        val (s0, v) = transfer(st, obj)
        s0.keysOf(v)
      case ENotSupported(msg) =>
        ??? // TODO need discussion
    }

    // transfer function for reference values
    def transfer(st: AbsState, ref: Ref): (AbsState, AbsRefValue) = ???

    // transfer function for unary operators
    def transfer(uop: UOp): AbsValue => AbsValue = v => uop match {
      case ONeg => ???
      case ONot => ???
      case OBNot => ???
    }

    // transfer function for binary operators
    def transfer(bop: BOp): (AbsValue, AbsValue) => AbsValue = (l, r) => bop match {
      case OPlus => ???
      case OSub => ???
      case OMul => ???
      case OPow => ???
      case ODiv => ???
      case OUMod => ???
      case OMod => ???
      case OLt => ???
      case OEq => ???
      case OEqual => ???
      case OAnd => ???
      case OOr => ???
      case OXor => ???
      case OBAnd => ???
      case OBOr => ???
      case OBXOr => ???
      case OLShift => ???
      case OSRShift => ???
      case OURShift => ???
    }

    // TODO pruning abstract states using conditions
    def prune(st: AbsState, expr: Expr, cond: Boolean): AbsState = st
  }
}
