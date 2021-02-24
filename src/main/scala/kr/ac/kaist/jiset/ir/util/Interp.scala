package kr.ac.kaist.jiset.ir

import java.text.Normalizer._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.util.StateUpdater
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
  type Result[T] = StateUpdater[T, State]
  implicit def pure[T](v: T): Result[T] = st => (v, st)
  val allocList: List[Value] => Result[Addr] = vlist => st => st.allocList(vlist)
  def allocMap(ty: Ty): List[(Value, Value)] => Result[Addr] =
    mlist => st => st.allocMap(ty, mlist.map({
      case (Str(s), v) => (s -> v)
      case _ => error(s"Non String key given")
    }).toMap)
  val allocSymbol: Value => Result[Addr] = v => st => v match {
    case Str(s) => st.allocSymbol(s)
    case _ => error(s"Non string symbol given")
  }
  val copyHelper: Value => Result[Addr] = v => st => v match {
    case v: Addr => st.copyObj(v)
    case _ => error(s"None address object for copy given")
  }

  // instructions
  def interp(inst: Inst): State => State = st => preinterp(inst) match {
    // conditional instructions
    case IIf(cond, thenInst, elseInst) => ???
    case IWhile(cond, body) => ???
    // call instructions
    case IApp(id, fexpr, args) => ???
    case IAccess(id, bexpr, expr) => ???
    // normal instuctions
    case IExpr(expr) => ???
    case ILet(id, expr) => ???
    case IAssign(ref, expr) => ???
    case IDelete(ref) => ???
    case IAppend(expr, list) => ???
    case IPrepend(expr, list) => ???
    case IReturn(expr) => ???
    case IThrow(id) => ???
    case ISeq(newInsts) => newInsts.foldLeft(st) {
      case (s0, inst) => interp(inst)(s0)
    }
    case IAssert(expr) => {
      val (v, s0) = interp(expr)(st)
      v match {
        case Bool(true) => s0
        case Bool(false) => error(s"assertion failure: ${beautify(expr)}")
        case _ => error(s"assertion is not a boolean: $v")
      }
    }
    case IPrint(expr) => {
      val (v, s0) = interp(expr)(st)
      if (!silent) Helper.print(s0, v)
      s0
    }
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
    case EPop(list, idx) => ???
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
    case EContains(list, elem) => ???
    case EReturnIfAbrupt(expr, check) => ???
    case ENotSupported(msg) =>
      error(s"Not Supported: $msg")
    // allocation expressions
    case EMap(ty, props) => for {
      mlist <- props.foldLeft(pure(List.empty[(Value, Value)])) {
        case (updater, (e1, e2)) => for {
          l <- updater
          v1 <- interp(e1)
          v2 <- interp(e2)
        } yield l :+ (v1, v2)
      }
      addr <- mlist ~> allocMap(ty)
    } yield addr
    case EList(exprs) => for {
      vlist <- exprs.foldLeft(pure(List.empty[Value])) {
        case (updater, expr) => for {
          l <- updater
          v <- interp(expr)
        } yield l :+ v
      }
      addr <- vlist ~> allocList
    } yield addr
    case ESymbol(desc) => for {
      v <- interp(desc)
      addr <- v ~> allocSymbol
    } yield addr
    case ECopy(expr) => for {
      v <- interp(expr)
      addr <- v ~> copyHelper
    } yield addr
    case EKeys(mobj) => ???
  }

  // references
  def interp(ref: Ref): Result[RefValue] = ref match {
    case RefId(id) => pure(RefValueId(id.name))
    case RefProp(ref, expr) => for {
      rv <- interp(ref)
      bv <- interp(rv)
      pv <- interp(expr)
    } yield (bv, pv) match {
      case (bv: Addr, pv: Str) => RefValueProp(bv, pv.str)
      case (bv: Str, pv: Str) => RefValueString(bv, pv.str)
      case _ => error(s"Not expected type in RefProp: ${beautify(expr)}")
    }

  }
  def interp(refV: RefValue): Result[Value] = st => refV match {
    case RefValueId(id) => ???
    case RefValueProp(addr, value) => ???
    case RefValueString(str, value) => ???
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
  private def modulo(l: BigInt, r: BigInt): BigInt = l % r
  private def unsigned_modulo(l: BigInt, r: BigInt): BigInt = {
    val m = l % r
    if (m * r < 0) m + r
    else m
  }
  private def modulo(l: BigInt, r: Long): BigInt = l % r
  private def unsigned_modulo(l: BigInt, r: Long): BigInt = {
    val m = l % r
    if (m * r < 0) m + r
    else m
  }
  private def modulo(l: Double, r: Double): Double = l % r
  private def unsigned_modulo(l: Double, r: Double): Double = {
    val m = l % r
    if (m * r < 0.0) m + r
    else m
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

  // catch and set undefined
  def catchUndef(v: => Value): Value = try v catch { case _: Throwable => Undef }

  // string operations
  def stringOp(str: String, prop: Value): Value = prop match {
    case Str("length") => INum(str.length)
    case Str("normNFC") => Str(normalize(str, Form.NFC))
    case Str("normNFD") => Str(normalize(str, Form.NFD))
    case Str("normNFKC") => Str(normalize(str, Form.NFKC))
    case Str("normNFKD") => Str(normalize(str, Form.NFKD))
    case INum(k) => Str(str(k.toInt).toString)
    case Num(k) => Str(str(k.toInt).toString)
    case v => error(s"wrong access of string reference: $str.$v")
  }
}
