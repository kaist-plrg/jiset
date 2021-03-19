package kr.ac.kaist.jiset.analyzer

import scala.concurrent.duration._
import kr.ac.kaist.jiset.ir.Beautifier._
import kr.ac.kaist.jiset.ir.util.ESValueParser
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.StateMonad
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec

// concrete transfer function
class Transfer(
  sem: Semantics,
  isDebug: Boolean = false,
  silent: Boolean = false,
  timeLimit: Option[Long] = None
) {
  // result of concrete transfer
  val monad = new StateMonad[State]
  import monad._

  // transfer information
  var startTime: Long = 0
  var instCount = 0

  // compute
  @tailrec
  final def compute: Unit = sem.next match {
    case Some(inst) => {
      sem.setState(transfer(inst)(sem.getState))
      compute
    }
    case None =>
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Transfer
  ////////////////////////////////////////////////////////////////////////////////

  // instructions
  def transfer(inst: Inst): Updater = preprocess(inst) match {
    // conditional instructions
    case IIf(cond, thenInst, elseInst) => for {
      e <- transfer(cond)
    } yield e match {
      case Bool(b) =>
        val nextInst = if (b) thenInst else elseInst
        sem.prepend(nextInst)
      case v => error(s"not a boolean: $v")
    }
    case IWhile(cond, body) => for {
      e <- transfer(cond)
    } yield e match {
      case Bool(false) =>
      case Bool(true) => sem.prependAll(List(body, inst))
      case v => error(s"not a boolean: $v")
    }
    // call instructions
    case IApp(id, fexpr, args) => for {
      fv <- transfer(fexpr)
      func = fv.to[Clo]
      argsv <- join(args.map(transfer))
      _ <- modify(_ => sem.doCall(func.fid, argsv, id))
    } yield ()
    case IReturn(expr) => for {
      v <- transfer(expr)
      _ <- modify(_ => sem.doReturn(v))
    } yield ()
    case IAccess(id, bexpr, expr, args) => ???
    // normal instuctions
    case IExpr(expr) => transfer(expr)
    case ILet(id, expr) => for {
      v <- transfer(expr)
      _ <- modify(_.define(id, v))
    } yield ()
    case IAssign(ref, expr) => for {
      rv <- transfer(ref)
      v <- transfer(expr)
      _ <- modify(_.updated(rv, v))
    } yield ()
    case IDelete(ref) => for {
      rv <- transfer(ref)
      _ <- modify(_.deleted(rv))
    } yield ()
    case IAppend(expr, list) => for {
      v <- transfer(expr)
      lv <- transfer(list)
      _ <- modify(_.append(lv.to[Addr], v))
    } yield ()
    case IPrepend(expr, list) => for {
      v <- transfer(expr)
      lv <- transfer(list)
      _ <- modify(_.prepend(lv.to[Addr], v))
    } yield ()
    case IThrow(id) => ???
    case IWithCont(id, params, body) => ???
    case ISeq(insts) => pure(sem.prependAll(insts))
    case IAssert(expr) => transfer(expr) map {
      case Bool(true) =>
      case Bool(false) => error(s"assertion failure: ${expr.beautified}")
      case v @ _ => error(s"assertion is not a boolean: $v")
    }
    case IPrint(expr) => for {
      v <- transfer(expr)
    } yield if (!silent) println(v)
    case ISetType(expr, ty) => for {
      v <- transfer(expr)
      _ <- modify(_.setType(v.to[Addr], ty))
    } yield ()
  }

  // expresssions
  def transfer(expr: Expr): Result[Value] = expr match {
    case ENum(n) => Num(n)
    case EINum(n) => INum(n)
    case EBigINum(b) => BigINum(b)
    case EStr(str) => Str(str)
    case EBool(b) => Bool(b)
    case EUndef => Undef
    case ENull => Null
    case EAbsent => Absent
    case EPop(list, idx) => (transfer(list) ~ transfer(idx)) flatMap {
      case (a: Addr, i) => _.pop(a, i)
      case _ => error(s"Not an address")
    }
    case ERef(ref) => for {
      rv <- transfer(ref)
      v <- transfer(rv)
    } yield v
    case ECont(params, body) => ???
    // logical operations
    case EUOp(uop, expr) => for {
      v <- transfer(expr)
    } yield calc(uop)(v)
    case EBOp(OAnd, left, right) => shortCircuit(true, left, right)
    case EBOp(OOr, left, right) => shortCircuit(false, left, right)
    case EBOp(bop, left, right) => for {
      lv <- transfer(left)
      rv <- transfer(right)
    } yield calc(bop)(lv, rv)
    case ETypeOf(expr) => for {
      v <- transfer(expr)
      st <- get
    } yield Str(typeof(v, st))
    case EIsCompletion(expr) => ???
    case EIsInstanceOf(base, kind) => ???
    case EGetElems(base, kind) => ???
    case EGetSyntax(base) => ???
    case EParseSyntax(code, rule, flags) => ???
    // TODO refactor util/{ESValueParser, Heler}.scala
    case EConvert(expr, cop, l) => (transfer(expr) ~ join(l.map(transfer))) map {
      case (Str(s), _) if cop == CStrToNum => Num(ESValueParser.str2num(s))
      // TODO Str -> BigInt parser
      case (Str(s), _) if cop == CStrToBigInt => ???
      case (INum(n), lvs) if cop == CNumToStr => {
        val radix = lvs.headOption.getOrElse(INum(10)) match {
          case INum(n) => n.toInt
          case Num(n) => n.toInt
          case _ => error(s"Radix not int")
        }
        Str(toStringHelper(n, radix))
      }
      case (INum(n), lvs) if cop == CNumToInt => INum(n)
      // TODO Int -> BigInt parser
      case (Num(d), lvs) if cop == CNumToBigInt => ???
      case (BigINum(bi), lvs) if cop == CBigIntToNum => ???
      case _ => error(s"Type and COp missmatch for EConvert: ${expr.beautified}")
    }
    case EContains(list, elem) => (transfer(list) ~ transfer(elem)) flatMap {
      case (a: Addr, elem) => _.contains(a, elem)
      case _ => error(s"Not an address")
    }
    case EReturnIfAbrupt(expr, check) => ???
    case ENotSupported(msg) => error(s"Not Supported: $msg")
    // allocation expressions
    case EMap(ty, props) => join(props.map {
      case (e1, e2) => transfer(e1) ~ transfer(e2)
    }) map {
      _.map {
        case (Str(s), v) => (s -> v)
        case _ => error(s"Non String key given")
      }.toMap
    } flatMap { mlist => _.allocMap(ty, mlist) }
    case EList(exprs: List[Expr]) => for {
      vs <- join(exprs.map(transfer))
      a <- id(_.allocList(vs))
    } yield a
    case ESymbol(desc) => for {
      v <- transfer(desc)
      a <- id(_.allocSymbol(v.to[Str]))
    } yield a
    case ECopy(expr) => for {
      v <- transfer(expr)
      a <- id(_.copyObj(v.to[Addr]))
    } yield a
    case EKeys(mobj) => for {
      v <- transfer(mobj)
      a <- id(_.mapObjKeys(v.to[Addr]))
    } yield a
  }

  // references
  def transfer(ref: Ref): Result[RefValue] = ref match {
    case RefId(id) => RefValueId(id.name)
    case RefProp(ref, expr) => for {
      rv <- transfer(ref)
      bv <- transfer(rv)
      pv <- transfer(expr)
    } yield (bv, pv) match {
      case (bv: Addr, pv: Str) => RefValueProp(bv, pv.str)
      case (bv: Addr, pv: INum) => RefValueProp(bv, pv.long.toString)
      case (bv: Str, pv: Str) => RefValueString(bv, pv.str)
      case (bv: Str, pv: INum) => RefValueString(bv, pv.long.toString)
      case _ => error(s"Not expected type in RefProp: ${expr.beautified}")
    }

  }
  def transfer(refV: RefValue): Result[Value] = refV match {
    // TODO use global
    case RefValueId(id) => _.getOrElse(id, sem.globals.getOrElse(id, Absent))
    case RefValueProp(addr, prop) => _.get(addr, prop)
    case RefValueString(Str(str), prop) => _.get(str, prop)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Helper
  ////////////////////////////////////////////////////////////////////////////////

  // preprocess
  private def preprocess(inst: Inst): Inst = {
    if (instCount == 0) startTime = System.currentTimeMillis
    instCount = instCount + 1
    if (instCount % 10000 == 0) timeLimit match {
      case Some(timeout) => if ((System.currentTimeMillis - startTime) > timeout * 1000) throw error("[Timeout]")
      case _ => ()
    }
    if (isDebug) inst match {
      case ISeq(_) =>
      case _ => println(s"transfer: ${inst.beautified}")
    }
    inst
  }

  // unary operators
  private def calc(uop: UOp): Value => Value = (uop, _) match {
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
  private def calc(bop: BOp): (Value, Value) => Value = (bop, _, _) match {
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

  // typeof
  private def typeof(v: Value, st: State): String = v match {
    case Num(_) | INum(_) => "Number"
    case Completion(_, v, _) => typeof(v, st)
    case Const(c) => "Constant"
    case BigINum(_) => "BigInt"
    case Str(_) => "String"
    case Bool(_) => "Boolean"
    case Undef => "Undefined"
    case Null => "Null"
    case Absent => "Absent"
    case a: Addr => st.get(a)._1 match {
      case _: ListObj => "List"
      case _: SymbolObj => "Symbol"
      case MapObj(ty, _) => ty.name
    }
    case _: Loc => ???
    case ASTVal(_) => "AST"
    case Cont() => "Continuation"
    case Clo(_, _) => "Closure"
  }

  // short circuit evaluation
  def shortCircuit(isAnd: Boolean, left: Expr, right: Expr): Result[Value] =
    for {
      lv <- transfer(left)
      b = lv.to[Bool]
      rv <- b match {
        case Bool(`isAnd`) => transfer(right)
        case _ => pure(b)
      }
    } yield {
      val op = if (isAnd) OAnd else OOr
      calc(op)(lv, rv)
    }
}
