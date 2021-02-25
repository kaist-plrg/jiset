package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.util._

// abstract transfer function
class AbsTransfer(sem: AbsSemantics) {
  import sem.cfg._

  // result of abstract transfer
  val monad = new StateMonad[AbsState]
  import monad._

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
      case (block: Block) => for {
        _ <- join(block.insts.map(transfer))
        st <- get
      } sem += NodePoint(next(block), view) -> st
      case call @ Call(inst) => for {
        _ <- transfer(inst)
        st <- get
      } sem += NodePoint(next(call), view) -> st
      case branch @ Branch(expr) => for {
        v <- transfer(expr)
        st <- get
      } v.bool.toSet.foreach {
        case true =>
          sem += NodePoint(thenNext(branch), view) -> prune(expr, true)(st)
        case false =>
          sem += NodePoint(elseNext(branch), view) -> prune(expr, false)(st)
      }
    }
  }

  // transfer function for return points
  def apply(rp: ReturnPoint): Unit = {
    // TODO handle inter-procedural cases
  }

  private class Helper(ret: ReturnPoint) {
    // transfer function for normal instructions
    def transfer(inst: NormalInst): Updater = ???
    // inst match {
    //   case IExpr(expr) =>
    //     val (s0, _) = transfer(st, expr)
    //     s0
    //   case ILet(id, expr) =>
    //     val (s0, v) = transfer(st, expr)
    //     s0 + (id.name -> v)
    //   case IAssign(ref, expr) =>
    //     val (s0, refv) = transfer(st, ref)
    //     val (s1, v) = transfer(s0, refv)
    //     s1.update(sem.globals, refv, v)
    //   case IDelete(ref) =>
    //     val (s0, refv) = transfer(st, ref)
    //     s0.delete(sem.globals, refv)
    //   case IAppend(expr, list) =>
    //     val (s0, v) = transfer(st, expr)
    //     val (s1, l) = transfer(s0, list)
    //     s1.append(v, l.addr)
    //   case IPrepend(expr, list) =>
    //     val (s0, v) = transfer(st, expr)
    //     val (s1, l) = transfer(s0, list)
    //     s1.prepend(v, l.addr)
    //   case IReturn(expr) =>
    //     val (s0, v) = transfer(st, expr)
    //     sem.doReturn(ret -> (s0.heap, v))
    //     AbsState.Bot
    //   case IThrow(id) => ???
    //   case IAssert(expr) => ???
    //   case IPrint(expr) => ???
    //   case IWithCont(id, params, bodyInst) => ???
    //   case ISetType(expr, ty) => ???
    // }

    // transfer function for call instructions
    def transfer(inst: CallInst): Updater = ???
    // inst match {
    //   case IApp(Id(x), fexpr, args) =>
    //     val (s0, f) = transfer(st, fexpr)
    //     ???
    //   case IAccess(Id(x), bexpr, expr) =>
    //     val (s0, b) = transfer(st, bexpr)
    //     val (s1, p) = transfer(s0, expr)
    //     val v: AbsValue = (b.getSingle, p.getSingle) match {
    //       case (One(ASTVal(ast)), One(Str(name))) => (ast, name) match {
    //         case ("NumericLiteral", "NumericValue") => numTop
    //         case ("StringLiteral", "StringValue") => strTop
    //         case _ => ???
    //       }
    //       case _ => ???
    //     }
    //     (st + (x -> v), Nil) // TODO handling call cases
    // }

    // transfer function for expressions
    // TODO consider the completion records
    def transfer(expr: Expr): Result[AbsValue] = ???
    // expr match {
    //   case ENum(n) => (st, AbsNum(n))
    //   case EINum(n) => (st, AbsINum(n))
    //   case EBigINum(b) => (st, AbsBigINum(b))
    //   case EStr(str) => (st, AbsStr(str))
    //   case EBool(b) => (st, AbsBool(b))
    //   case EUndef => (st, AbsUndef.Top)
    //   case ENull => (st, AbsNull.Top)
    //   case EAbsent => (st, AbsAbsent.Top)
    //   case EMap(ty, props) =>
    //     // TODO handling type information
    //     val (newSt, map) = props.foldLeft((st, Map[String, AbsValue]())) {
    //       case ((s0, map), (EStr(k), expr)) =>
    //         val (s1, v) = transfer(s0, expr)
    //         (s1, map + (k -> v))
    //       case _ => ??? // TODO handling non-string keys
    //     }
    //     newSt.allocMap(map)
    //   case EList(exprs) =>
    //     val (newSt, vs) = exprs.foldLeft((st, Vector[AbsValue]())) {
    //       case ((s0, vs), expr) =>
    //         val (s1, v) = transfer(s0, expr)
    //         (s1, vs :+ v)
    //     }
    //     newSt.allocList(vs.toList)
    //   case ESymbol(desc) => desc match {
    //     case EStr(desc) => st.allocSymbol(desc)
    //     case _ => ??? // TODO handling non-string descriptions
    //   }
    //   case EPop(list, idx) =>
    //     val (s0, l) = transfer(st, list)
    //     val (s1, k) = transfer(s0, idx)
    //     s1.pop(l, k)
    //   case ERef(ref) =>
    //     val (s0, refv) = transfer(st, ref)
    //     (s0, s0(sem.globals, refv))
    //   case ECont(params, body) => ???
    //   case EUOp(uop, expr) =>
    //     val (s0, v) = transfer(st, expr)
    //     (s0, transfer(uop)(v))
    //   case EBOp(bop, left, right) =>
    //     val (s0, l) = transfer(st, left)
    //     val (s1, r) = transfer(st, right)
    //     (s1, transfer(bop)(l, r))
    //   case ETypeOf(expr) =>
    //     val (s0, v) = transfer(st, expr)
    //     (s0, s0.typeOf(v))
    //   case EIsCompletion(expr) =>
    //     val (s0, v) = transfer(st, expr)
    //     ??? // TODO after discussing the completion structures
    //   case EIsInstanceOf(base, name) =>
    //     val (s0, v) = transfer(st, expr)
    //     ??? // TODO need discussion
    //   case EGetElems(base, name) =>
    //     val (s0, v) = transfer(st, expr)
    //     ??? // TODO need discussion
    //   case EGetSyntax(base) => (st, AbsStr.Top) // TODO handling non-AST values
    //   case EParseSyntax(code, rule, flags) =>
    //     val (s0, c) = transfer(st, code)
    //     val (s1, r) = transfer(s0, rule)
    //     // XXX maybe flags are not necessary in abstract semantics
    //     ???
    //   case EConvert(source, target, flags) =>
    //     val (s0, v) = transfer(st, source)
    //     ??? // TODO need discussion
    //   case EContains(list, elem) =>
    //     val (s0, l) = transfer(st, list)
    //     val (s1, e) = transfer(s0, elem)
    //     (s1, s1.contains(l, e))
    //   case EReturnIfAbrupt(expr, check) =>
    //     transfer(st, expr) // TODO support abrupt completion check
    //   case ECopy(obj) =>
    //     val (s0, v) = transfer(st, obj)
    //     s0.copyOf(v)
    //   case EKeys(obj) =>
    //     val (s0, v) = transfer(st, obj)
    //     s0.keysOf(v)
    //   case ENotSupported(msg) =>
    //     ??? // TODO need discussion
    // }

    // transfer function for reference values
    def transfer(ref: Ref): Result[AbsRefValue] = ???
    // ref match {
    //   case RefId(id) => (st, AbsRefValue.Id(id.name))
    //   case RefProp(ref, expr) =>
    //     val (s0, rv) = transfer(st, ref)
    //     val (s1, b) = transfer(s0, rv)
    //     val (s2, p) = transfer(s0, expr)
    //     (s2, AbsRefValue(b, p.str)) // TODO handle non-string properties
    // }

    // transfer function for reference values
    def transfer(refv: AbsRefValue): Result[AbsValue] = ???

    // transfer function for unary operators
    // TODO more precise abstract semantics
    def transfer(uop: UOp): AbsValue => AbsValue = v => uop match {
      case ONeg => numTop
      case ONot => !v.bool
      case OBNot => intTop
    }

    // all booleans
    val boolTop: AbsValue = AbsBool.Top

    // transfer function for binary operators
    // TODO more precise abstract semantics
    def transfer(bop: BOp): (AbsValue, AbsValue) => AbsValue = (l, r) => bop match {
      case OPlus => arithTop
      case OSub => arithTop
      case OMul => arithTop
      case OPow => numTop
      case ODiv => numTop
      case OUMod => numTop
      case OMod => numTop
      case OLt => boolTop
      case OEq => boolTop
      case OEqual => boolTop
      case OAnd => l.bool && r.bool
      case OOr => l.bool || r.bool
      case OXor => l.bool ^ r.bool
      case OBAnd => intTop
      case OBOr => intTop
      case OBXOr => intTop
      case OLShift => intTop
      case OSRShift => intTop
      case OURShift => AbsINum.Top
    }

    // TODO pruning abstract states using conditions
    def prune(expr: Expr, cond: Boolean): Updater = st => st

    // all integers
    private val intTop: AbsValue = AbsPrim(
      int = AbsINum.Top,
      bigint = AbsBigINum.Top
    )

    // all numbers
    private val numTop: AbsValue = AbsPrim(
      num = AbsNum.Top,
      int = AbsINum.Top,
      bigint = AbsBigINum.Top
    )

    // all strings
    private val strTop: AbsValue = AbsStr.Top

    // all arithmetic values
    private val arithTop: AbsValue = AbsPrim(
      num = AbsNum.Top,
      int = AbsINum.Top,
      bigint = AbsBigINum.Top,
      str = AbsStr.Top
    )
  }
}
