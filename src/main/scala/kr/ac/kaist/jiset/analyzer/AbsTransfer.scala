package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.analyzer.domain._
import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js._
import kr.ac.kaist.jiset.spec.algorithm._
import scala.annotation.tailrec

// abstract transfer function
case class AbsTransfer(sem: AbsSemantics) {
  import AbsState.monad._

  // transfer function for control points
  def apply(cp: ControlPoint): Unit = cp match {
    case (np: NodePoint[_]) => this(np)
    case (rp: ReturnPoint) => this(rp)
  }

  // transfer function for node points
  def apply[T <: Node](np: NodePoint[T]): Unit = {
    val st = sem(np)
    val NodePoint(node, view) = np
    val helper = new Helper(np)
    node match {
      case (entry: Entry) =>
        sem += NodePoint(cfg.nextOf(entry), view) -> st
      case (exit: Exit) => ???
      case (normal: Normal) => ???
      case (call: Call) => helper.transfer(call.inst)(st)
      case arrow @ Arrow(_, inst, fid) => ???
      case branch @ Branch(_, inst) => ???
    }
  }

  // transfer function for return points
  def apply(rp: ReturnPoint): Unit = ???

  // internal transfer function with a specific view
  private class Helper(val cp: ControlPoint) {
    // transfer function for normal instructions
    def transfer(inst: NormalInst): Updater = inst match {
      case IExpr(expr) => ???
      case ILet(id, expr) => ???
      case IAssign(ref, expr) => ???
      case IDelete(ref) => ???
      case IAppend(expr, list) => ???
      case IPrepend(expr, list) => ???
      case IReturn(expr) => ???
      case IThrow(name) => ???
      case IAssert(expr) => ???
      case IPrint(expr) => ???
    }

    // transfer function for call instructions
    def transfer(inst: CallInst): Updater = inst match {
      // TODO `simpleFuncs contains name` case
      case IApp(id, fexpr, args) => for {
        value <- transfer(fexpr)
        vs <- join(args.map(transfer))
        _ <- join(value.func.toList.map(transfer(_, vs)))
        _ <- join(value.clo.toList.map(transfer(_, vs)))
        _ <- join(value.cont.toList.map(transfer(_, vs)))
      } yield ???
      case IAccess(id, bexpr, expr, args) => ???
    }

    // transfer function for functions
    def transfer(func: Function, vs: List[AbsValue]): Updater = ???

    // transfer function for closures
    def transfer(clo: AClo, vs: List[AbsValue]): Updater = ???

    // transfer function for continuations
    def transfer(cont: ACont, vs: List[AbsValue]): Updater = ???

    // transfer function for expressions
    def transfer(expr: Expr): Result[AbsValue] = expr match {
      case ENum(n) => ???
      case EINum(n) => ???
      case EBigINum(b) => ???
      case EStr(str) => ???
      case EBool(b) => ???
      case EUndef => ???
      case ENull => ???
      case EAbsent => ???
      case EMap(ty, props) => ???
      case EList(exprs) => ???
      case ESymbol(desc) => ???
      case EPop(list, idx) => ???
      case ERef(ref) => for {
        rv <- transfer(ref)
        v <- transfer(rv)
      } yield v
      case EUOp(uop, expr) => ???
      case EBOp(OAnd, left, right) => ???
      case EBOp(OOr, left, right) => ???
      case EBOp(OEq, ERef(RefId(id)), EAbsent) => ???
      case EBOp(bop, left, right) => ???
      case ETypeOf(expr) => ???
      case EIsCompletion(expr) => ???
      case EIsInstanceOf(base, name) => ???
      case EGetElems(base, name) => ???
      case EGetSyntax(base) => ???
      case EParseSyntax(code, rule, parserParams) => ???
      case EConvert(source, target, flags) => ???
      case EContains(list, elem) => ???
      case EReturnIfAbrupt(rexpr @ ERef(ref), check) => ???
      case EReturnIfAbrupt(expr, check) => ???
      case ECopy(obj) => ???
      case EKeys(mobj, intSorted) => ???
      case ENotSupported(msg) => ???
    }

    // transfer function for references
    def transfer(ref: Ref): Result[AbsRefValue] = ref match {
      case RefId(id) => AbsRefId(id)
      case RefProp(ref, expr) => for {
        rv <- transfer(ref)
        b <- transfer(rv)
        p <- transfer(expr)
      } yield AbsRefProp(b, p)
    }

    // transfer function for reference values
    def transfer(rv: AbsRefValue): Result[AbsValue] = for {
      v <- get(_(rv, cp))
    } yield v

    // get initial local variables
    def getLocals(
      params: List[Param],
      vs: List[AbsValue]
    ): Map[Id, AbsValue] = ???
  }
}
