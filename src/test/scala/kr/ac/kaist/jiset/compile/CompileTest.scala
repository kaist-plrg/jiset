package kr.ac.kaist.jiset

import kr.ac.kaist.ires.ir._
import org.scalatest._

abstract class CompileTest extends JISETTest {
  // compare results
  def isTempId(id: Id): Boolean = id.name.startsWith("__")
  def compare[T](
    result: List[T],
    answer: List[T]
  )(implicit tCompare: (T, T) => Boolean): Boolean =
    (result.length == answer.length) && (result zip answer).forall {
      case (l, r) => tCompare(l, r)
    }
  implicit def compare(result: Id, answer: Id): Boolean =
    (isTempId(result) && isTempId(answer)) || (result == answer)
  implicit def compare(result: Ty, answer: Ty): Boolean = result == answer
  def compare(result: Inst, answer: Inst): Boolean = (result, answer) match {
    case (_, IExpr(ENotYetModeled(_) | ENotSupported(_))) => true
    case (IIf(lc, lt, le), IIf(rc, rt, re)) =>
      compare(lc, rc) && compare(lt, rt) && compare(le, re)
    case (IWhile(lc, lb), IWhile(rc, rb)) =>
      compare(lc, rc) && compare(lb, rb)
    case (IExpr(le), IExpr(re)) =>
      compare(le, re)
    case (ILet(li, le), ILet(ri, re)) =>
      compare(li, ri) && compare(le, re)
    case (IAssign(lr, le), IAssign(rr, re)) =>
      compare(lr, rr) && compare(le, re)
    case (IDelete(lr), IDelete(rr)) =>
      compare(lr, rr)
    case (IAppend(le, ll), IAppend(re, rl)) =>
      compare(le, re) && compare(ll, rl)
    case (IPrepend(le, ll), IPrepend(re, rl)) =>
      compare(le, re) && compare(ll, rl)
    case (IReturn(le), IReturn(re)) =>
      compare(le, re)
    case (ISeq(li), ISeq(ri)) =>
      val (success, remain) = ri.foldLeft((true, li)) {
        case ((false, _), _) | ((true, Nil), _) => (false, Nil)
        case ((true, ls), IExpr(ENotYetModeled(_) | ENotSupported(_))) => (true, ls)
        case ((true, ls), r) =>
          var remain = ls
          while (!remain.isEmpty && !compare(remain.head, r)) remain = remain.tail
          if (remain.isEmpty) (false, Nil)
          else (true, remain.tail)
      }
      success
    case (IAssert(le), IAssert(re)) =>
      compare(le, re)
    case (IPrint(le), IPrint(re)) =>
      compare(le, re)
    case (IApp(li, lf, la), IApp(ri, rf, ra)) =>
      compare(li, ri) && compare(lf, rf) && compare(la, ra)
    case (IAccess(li, lb, le), IAccess(ri, rb, re)) =>
      compare(li, ri) && compare(lb, rb) && compare(le, re)
    case (IWithCont(li, lp, lb), IWithCont(ri, rp, rb)) =>
      compare(li, ri) && compare(lp, rp) && compare(lb, rb)
    case (ISetType(le, lt), ISetType(re, rt)) =>
      compare(le, re) && compare(lt, rt)
    case _ => false
  }
  implicit def compare(result: Expr, answer: Expr): Boolean = (result, answer) match {
    case (ln: ENum, rn: ENum) => ln == rn
    case (EINum(ln), EINum(rn)) => ln == rn
    case (EBigINum(lb), EBigINum(rb)) => lb == rb
    case (EStr(ls), EStr(rs)) => ls == rs
    case (EBool(lb), EBool(rb)) => lb == rb
    case (EUndef, EUndef) => true
    case (ENull, ENull) => true
    case (EAbsent, EAbsent) => true
    case (EMap(lt, lp), EMap(rt, rp)) => compare(lt, rt) && compare(lp, rp) {
      case ((lf, ls), (rf, rs)) => compare(lf, rf) && compare(ls, rs)
    }
    case (EList(le), EList(re)) => compare(le, re)
    case (ESymbol(ld), ESymbol(rd)) => compare(ld, rd)
    case (EPop(ll, li), EPop(rl, ri)) => compare(ll, rl) && compare(li, ri)
    case (ERef(lr), ERef(rr)) => compare(lr, rr)
    case (ECont(lp, lb), ECont(rp, rb)) => compare(lp, rp) && compare(lb, rb)
    case (EUOp(lu, le), EUOp(ru, re)) => lu == ru && compare(le, re)
    case (EBOp(lb, ll, lr), EBOp(rb, rl, rr)) =>
      lb == rb && compare(ll, rl) && compare(lr, rr)
    case (ETypeOf(le), ETypeOf(re)) => compare(le, re)
    case (EIsCompletion(le), EIsCompletion(re)) => compare(le, re)
    case (EIsInstanceOf(lb, ln), EIsInstanceOf(rb, rn)) =>
      compare(lb, rb) && ln == rn
    case (EGetElems(lb, ln), EGetElems(rb, rn)) =>
      compare(lb, rb) && ln == rn
    case (EGetSyntax(lb), EGetSyntax(rb)) => compare(lb, rb)
    case (EParseSyntax(lc, lr, lf), EParseSyntax(rc, rr, rf)) =>
      compare(lc, rc) && compare(lr, rr) && compare(lf, rf)
    case (EConvert(ls, lt, lf), EConvert(rs, rt, rf)) =>
      compare(ls, rs) && lt == rt && compare(lf, rf)
    case (EContains(ll, le), EContains(rl, re)) =>
      compare(ll, rl) && compare(le, re)
    case (ECopy(lo), ECopy(ro)) => compare(lo, ro)
    case (EKeys(lm), EKeys(rm)) => compare(lm, rm)
    case (_, ENotYetModeled(_) | ENotSupported(_)) => true
    case _ => false
  }
  implicit def compare(result: Ref, answer: Ref): Boolean = (result, answer) match {
    case (RefId(x), RefId(y)) => compare(x, y)
    case (RefProp(lr, le), RefProp(rr, re)) =>
      compare(lr, rr) && compare(le, re)
    case _ => false
  }

  // test compile results
  def diffTest(filename: String, result: Inst, answer: Inst): Unit =
    assert(compare(result, answer))
}
