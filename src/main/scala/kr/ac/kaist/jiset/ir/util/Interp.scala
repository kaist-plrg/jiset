package kr.ac.kaist.jiset.ir

import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.StateMonad
import kr.ac.kaist.jiset.util.StateMonad._
import scala.annotation.tailrec
import scala.concurrent.duration._
import kr.ac.kaist.jiset.analyzer.INumT

// IR Interpreter
class Interp(
  isDebug: Boolean = false,
  silent: Boolean = false,
  timeLimit: Option[Long] = None
) {
  var startTime: Long = 0
  var instCount = 0
  var recentInst: Option[Inst] = None

  def apply(inst: Inst) = interp(inst)
  def apply(insts: List[Inst]) = interp(ISeq(insts))

  // preinterp
  def preinterp(inst: Inst): Inst = {
    recentInst = Some(inst)
    if (instCount == 0) startTime = System.currentTimeMillis
    instCount = instCount + 1
    if (instCount % 10000 == 0) timeLimit match {
      case Some(timeout) => if ((System.currentTimeMillis - startTime) > timeout * 1000) throw error("[Timeout]")
      case _ => ()
    }
    if (isDebug) inst match {
      case ISeq(_) =>
      case _ => println(s"Interp: ${beautify(inst)}")
    }
    inst
  }

  // interp result
  type Result[T] = StateMonad[T, State]

  // instructions
  def interp(inst: Inst): Result[Unit] = preinterp(inst) match {
    // conditional instructions
    case IIf(cond, thenInst, elseInst) => ???
    case IWhile(cond, body) => ???
    // call instructions
    case IApp(id, fexpr, args) => ???
    case IAccess(id, bexpr, expr) => ???
    // normal instuctions
    case IExpr(expr) => interp(expr) ^^^ ()
    case ILet(id, expr) => interp(expr) ^^ { case (v, st) => st.define(id, v) }
    case IAssign(ref, expr) => interp(ref) ~ interp(expr) ^^ {
      case ((rv, v), st) => st.updated(rv, v)
    }
    case IDelete(ref) => ???
    case IAppend(expr, list) => ???
    case IPrepend(expr, list) => ???
    case IReturn(expr) => ???
    case IThrow(id) => ???
    case ISeq(newInsts) => join(newInsts.map(interp)) ^^^ ()
    case IAssert(expr) => for {
      v <- interp(expr)
    } yield v match {
      case Bool(true) =>
      case Bool(false) => error(s"assertion failure: ${beautify(expr)}")
      case _ => error(s"assertion is not a boolean: $v")
    }
    case IPrint(expr) => interp(expr).map(v => if (!silent) println(v))
    case IWithCont(id, params, body) => ???
    case ISetType(expr, ty) => ???
  }

  // expresssions
  def interp(expr: Expr): Result[Value] = expr match {
    case ENum(n) => Num(n)
    case EINum(n) => INum(n)
    case EBigINum(b) => BigINum(b)
    case EStr(str) => Str(str)
    case EBool(b) => Bool(b)
    case EUndef => Undef
    case ENull => Null
    case EAbsent => Absent
    case EPop(list, idx) => (for {
      l <- interp(list)
      i <- interp(idx)
    } yield (l, i)) ^^ {
      case ((a: Addr, i), st) =>
        st.pop(a, i)
      case _ => error(s"Non Addr given for EPop: ${beautify(expr)}")
    }
    case ERef(ref) => for {
      rv <- interp(ref)
      v <- interp(rv)
    } yield v
    case ECont(params, body) => ???
    // logical operations
    case EUOp(uop, expr) => for {
      v <- interp(expr)
    } yield interp(uop)(v)
    /* case EBOp(OAnd, left, right) => ???
    case EBOp(OOr, left, right) => ??? */ // ? : why separate these two cases?
    case EBOp(bop, left, right) => for {
      lv <- interp(left)
      rv <- interp(right)
    } yield interp(bop)(lv, rv)
    case ETypeOf(expr) => for {
      v <- interp(expr)
    } yield v match {
      case Num(_) | INum(_) => Str("Number")
      case BigINum(_) => Str("BigInt")
      case Str(_) => Str("String")
      case Bool(_) => Str("Boolean")
      case Undef => Str("Undefined")
      case Null => Str("Null")
      case Absent => Str("Absent")
      case _ => ???
    }
    case EIsCompletion(expr) => ???
    case EIsInstanceOf(base, kind) => ???
    case EGetElems(base, kind) => ???
    case EGetSyntax(base) => ???
    case EParseSyntax(code, rule, flags) => ???
    case EConvert(expr, cop, l) => ???
    case EContains(list, elem) => (for {
      l <- interp(list)
      v <- interp(elem)
    } yield (l, v)) ^^ {
      case ((l: Addr, v), st) =>
        st.heap(l) match {
          case ListObj(vs) => (Bool(vs contains v), st)
          case _ => error("Not ListObj")
        }
      case _ => error(s"Not Addr given in ${beautify(expr)}")
    }
    case EReturnIfAbrupt(expr, check) => ???
    case ENotSupported(msg) =>
      error(s"Not Supported: $msg")
    // allocation expressions
    case EMap(ty, props) => join(props.map {
      case (e1, e2) => for {
        v1 <- interp(e1)
        v2 <- interp(e2)
      } yield (v1, v2)
    }) ^^ {
      case (mlist, s) => s.allocMap(ty, mlist.map({
        case (Str(s), v) => (s -> v)
        case _ => error(s"Non String key given")
      }).toMap)
    }
    case EList(exprs) => join(exprs.map(interp)) ^^ {
      case (l, s) => s.allocList(l)
    }
    case ESymbol(desc) => interp(desc) ^^ {
      case (v, st) => v match {
        case Str(s) => st.allocSymbol(s)
        case _ => error(s"Non string given for ESymbol: ${beautify(expr)}")
      }
    }
    case ECopy(expr) => interp(expr) ^^ {
      case (v, st) => v match {
        case v: Addr => st.copyObj(v)
        case _ => error(s"None address object for ECopy given: ${beautify(expr)}")
      }
    }
    case EKeys(mobj) => interp(expr) ^^ {
      case (v, st) => v match {
        case v: Addr => st.mapObjKeys(v)
        case _ => error(s"None map object for EKeys given: ${beautify(expr)}")
      }
    }
  }

  // references
  def interp(ref: Ref): Result[RefValue] = ref match {
    case RefId(id) => RefValueId(id.name)
    case RefProp(ref, expr) => for {
      rv <- interp(ref)
      bv <- interp(rv)
      pv <- interp(expr)
    } yield (bv, pv) match {
      case (bv: Addr, pv: Str) => RefValueProp(bv, pv.str)
      case (bv: Addr, pv: INum) => RefValueProp(bv, pv.long.toString)
      case (bv: Str, pv: Str) => RefValueString(bv, pv.str)
      case (bv: Str, pv: INum) => RefValueString(bv, pv.long.toString)
      case _ => error(s"Not expected type in RefProp: ${beautify(expr)}")
    }

  }
  def interp(refV: RefValue): Result[Value] = st => refV match {
    case RefValueId(id) => st.get(id)
    case RefValueProp(addr, prop) => st.get(addr, prop)
    case RefValueString(Str(str), prop) => st.get(str, prop)
  }

  // unary operators
  def interp(uop: UOp): Value => Value = (uop, _) match {
    case (ONeg, Num(n)) => Num(-n)
    case (ONeg, INum(n)) => INum(-n)
    case (ONeg, BigINum(b)) => BigINum(-b)
    case (ONot, Bool(b)) => Bool(!b)
    case (OBNot, Num(n)) => INum(~(n.toInt))
    case (OBNot, INum(n)) => INum(~n)
    case (OBNot, BigINum(b)) => BigINum(~b)
    case (_, value) => error(s"wrong type of value for the operator $uop: $value")
  }
  // binary operators
  def interp(bop: BOp): (Value, Value) => Value = (bop, _, _) match {
    // double operations
    case (OPlus, Num(l), Num(r)) => Num(l + r)
    case (OSub, Num(l), Num(r)) => Num(l - r)
    case (OMul, Num(l), Num(r)) => Num(l * r)
    case (OPow, Num(l), Num(r)) => Num(math.pow(l, r))
    case (ODiv, Num(l), Num(r)) => Num(l / r)
    case (OMod, Num(l), Num(r)) => Num(modulo(l, r))
    case (OUMod, Num(l), Num(r)) => Num(unsigned_modulo(l, r))
    case (OLt, Num(l), Num(r)) => Bool(l < r)

    // double with long operations
    case (OPlus, INum(l), Num(r)) => Num(l + r)
    case (OSub, INum(l), Num(r)) => Num(l - r)
    case (OMul, INum(l), Num(r)) => Num(l * r)
    case (ODiv, INum(l), Num(r)) => Num(l / r)
    case (OMod, INum(l), Num(r)) => Num(modulo(l, r))
    case (OPow, INum(l), Num(r)) => Num(math.pow(l, r))
    case (OUMod, INum(l), Num(r)) => Num(unsigned_modulo(l, r))
    case (OLt, INum(l), Num(r)) => Bool(l < r)
    case (OPlus, Num(l), INum(r)) => Num(l + r)
    case (OSub, Num(l), INum(r)) => Num(l - r)
    case (OMul, Num(l), INum(r)) => Num(l * r)
    case (ODiv, Num(l), INum(r)) => Num(l / r)
    case (OMod, Num(l), INum(r)) => Num(modulo(l, r))
    case (OPow, Num(l), INum(r)) => Num(math.pow(l, r))
    case (OUMod, Num(l), INum(r)) => Num(unsigned_modulo(l, r))
    case (OLt, Num(l), INum(r)) => Bool(l < r)

    // string operations
    case (OPlus, Str(l), Str(r)) => Str(l + r)
    case (OSub, Str(l), INum(r)) => Str(l.dropRight(r.toInt))
    case (OMul, Str(l), INum(r)) => Str(l * r.toInt)
    case (OLt, Str(l), Str(r)) => Bool(l < r)

    // long operations
    case (OPlus, INum(l), INum(r)) => INum(l + r)
    case (OSub, INum(l), INum(r)) => INum(l - r)
    case (OMul, INum(l), INum(r)) => INum(l * r)
    case (ODiv, INum(l), INum(r)) =>
      val x = l.toDouble / r.toDouble
      if (x.toLong == x) INum(x.toLong) else Num(x)
    case (OPow, INum(l), INum(r)) =>
      val x = math.pow(l, r)
      if (x.toLong == x) INum(x.toLong) else Num(x)
    case (OUMod, INum(l), INum(r)) => INum(unsigned_modulo(l, r).toLong)
    case (OMod, INum(l), INum(r)) => INum(modulo(l, r).toLong)
    case (OLt, INum(l), INum(r)) => Bool(l < r)
    case (OBAnd, INum(l), INum(r)) => INum(l.toInt & r.toInt)
    case (OBOr, INum(l), INum(r)) => INum(l.toInt | r.toInt)
    case (OBXOr, INum(l), INum(r)) => INum(l.toInt ^ r.toInt)
    case (OLShift, INum(l), INum(r)) => INum((l.toInt << r.toInt).toLong)
    case (OSRShift, INum(l), INum(r)) => INum((l.toInt >> r.toInt).toLong)
    case (OURShift, INum(l), INum(r)) => INum((l.toLong & 0xffffffffL) >>> r.toInt)

    // logical operations
    case (OAnd, Bool(l), Bool(r)) => Bool(l && r)
    case (OOr, Bool(l), Bool(r)) => Bool(l || r)
    case (OXor, Bool(l), Bool(r)) => Bool(l ^ r)

    // equality operations
    case (OEq, ASTVal(l), ASTVal(r)) => Bool(l eq r)
    case (OEq, ASTVal(l), Str(r)) => Bool(l.toString == r)
    case (OEq, Str(l), ASTVal(r)) => Bool(l == r.toString)
    case (OEq, INum(l), Num(r)) => Bool(!(r equals -0.0) && l == r)
    case (OEq, Num(l), INum(r)) => Bool(!(l equals -0.0) && l == r)
    case (OEq, Num(l), Num(r)) => Bool(l equals r)
    case (OEq, Num(l), BigINum(r)) => Bool(l == r)
    case (OEq, BigINum(l), Num(r)) => Bool(l == r)
    case (OEq, INum(l), BigINum(r)) => Bool(l == r)
    case (OEq, BigINum(l), INum(r)) => Bool(l == r)
    case (OEq, l, r) => Bool(l == r)

    // double equality operations
    case (OEqual, INum(l), Num(r)) => Bool(l == r)
    case (OEqual, Num(l), INum(r)) => Bool(l == r)
    case (OEqual, Num(l), Num(r)) => Bool(l == r)
    case (OEqual, l, r) => Bool(l == r)

    // double with big integers
    case (OLt, BigINum(l), Num(r)) =>
      Bool(new java.math.BigDecimal(l.bigInteger).compareTo(new java.math.BigDecimal(r)) < 0)
    case (OLt, BigINum(l), INum(r)) =>
      Bool(new java.math.BigDecimal(l.bigInteger).compareTo(new java.math.BigDecimal(r)) < 0)
    case (OLt, Num(l), BigINum(r)) =>
      Bool(new java.math.BigDecimal(l).compareTo(new java.math.BigDecimal(r.bigInteger)) < 0)
    case (OLt, INum(l), BigINum(r)) =>
      Bool(new java.math.BigDecimal(l).compareTo(new java.math.BigDecimal(r.bigInteger)) < 0)

    // big integers
    case (OPlus, BigINum(l), BigINum(r)) => BigINum(l + r)
    case (OLShift, BigINum(l), BigINum(r)) => BigINum(l << r.toInt)
    case (OSRShift, BigINum(l), BigINum(r)) => BigINum(l >> r.toInt)
    case (OSub, BigINum(l), BigINum(r)) => BigINum(l - r)
    case (OSub, BigINum(l), INum(r)) => BigINum(l - r)
    case (OMul, BigINum(l), BigINum(r)) => BigINum(l * r)
    case (ODiv, BigINum(l), BigINum(r)) => BigINum(l / r)
    case (OMod, BigINum(l), BigINum(r)) => BigINum(modulo(l, r))
    case (OUMod, BigINum(l), BigINum(r)) => BigINum(unsigned_modulo(l, r))
    case (OUMod, BigINum(l), INum(r)) => BigINum(unsigned_modulo(l, r))
    case (OLt, BigINum(l), BigINum(r)) => Bool(l < r)
    case (OBAnd, BigINum(l), BigINum(r)) => BigINum(l & r)
    case (OBOr, BigINum(l), BigINum(r)) => BigINum(l | r)
    case (OBXOr, BigINum(l), BigINum(r)) => BigINum(l ^ r)
    case (OPow, BigINum(l), BigINum(r)) => BigINum(l.pow(r.toInt))
    case (OPow, BigINum(l), INum(r)) => BigINum(l.pow(r.toInt))
    case (OPow, BigINum(l), Num(r)) =>
      if (r.toInt < 0) Num(math.pow(l.toDouble, r)) else BigINum(l.pow(r.toInt))

    case (_, lval, rval) => error(s"wrong type: $lval $bop $rval")
  }

  // short circuit evaluation
  def shortCircuit(
    bop: BOp,
    base: Boolean,
    op: (Boolean, Boolean) => Boolean,
    left: Expr,
    right: Expr,
    st: State
  ): (Value, State) = interp(left)(st) match {
    case pair @ (Bool(`base`), _) => pair
    case (lv, s0) =>
      val (rv, s1) = interp(right)(s0)
      (lv, rv) match {
        case (Bool(l), Bool(r)) => (Bool(op(l, r)), s1)
        case (lval, rval) => error(s"wrong type: $lval $bop $rval")
      }
  }
}
