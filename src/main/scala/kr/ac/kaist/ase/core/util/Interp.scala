package kr.ac.kaist.ase.core

// CORE Interpreter
object Interp {
  // perform transition until instructions are empty
  def fixpoint(st: State): State = st.insts match {
    case Nil => st
    case inst :: rest =>
      fixpoint(interp(inst)(st.copy(insts = rest)))
  }

  // instructions
  def interp(inst: Inst): State => State = st => {
    // TODO delete
    // inst match {
    //   case ISeq(_) =>
    //   case _ => println(s"${st.context}: ${beautify(inst)}")
    // }
    inst match {
      case IExpr(expr) =>
        val (_, s0) = interp(expr)(st)
        s0
      case ILet(id, expr) =>
        val (value, s0) = interp(expr)(st)
        s0.define(id, value)
      case IAssign(ref, expr) =>
        val (refV, s0) = interp(ref)(st)
        val (value, s1) = interp(expr)(s0)
        s1.updated(refV, value)
      case IDelete(ref) =>
        val (refV, s0) = interp(ref)(st)
        s0.deleted(refV)
      case IPush(expr, list) =>
        val (exprV, s0) = interp(expr)(st)
        val (listV, s1) = interp(list)(s0)
        listV match {
          case addr: Addr => s0.push(addr, exprV)
          case v => error(s"not an address: $v")
        }
      case IReturn(expr) =>
        val (value, s0) = interp(expr)(st)
        s0.copy(retValue = Some(value), insts = Nil)
      case IIf(cond, thenInst, elseInst) =>
        val (v, s0) = interp(cond)(st)
        v match {
          case Bool(true) => s0.copy(insts = thenInst :: s0.insts)
          case Bool(false) => s0.copy(insts = elseInst :: s0.insts)
          case v => error(s"not a boolean: $v")
        }
      case IWhile(cond, body) =>
        val (v, s0) = interp(cond)(st)
        v match {
          case Bool(true) => s0.copy(insts = body :: inst :: s0.insts)
          case Bool(false) => s0
          case v => error(s"not a boolean: $v")
        }
      case ISeq(newInsts) => st.copy(insts = newInsts ++ st.insts)
      case IAssert(expr) =>
        val (v, s0) = interp(expr)(st)
        v match {
          case Bool(true) => s0
          case Bool(false) => error(s"assertion failure: ${beautify(expr)}")
          case v => error(s"not a boolean: $v")
        }
      case IPrint(expr) =>
        val (v, s0) = interp(expr)(st)
        v match {
          case addr: Addr => println(beautify(s0.heap(addr)))
          case v => println(beautify(v))
        }
        s0
    }
  }

  // expresssions
  def interp(expr: Expr): State => (Value, State) = st => expr match {
    case ENum(n) => (Num(n), st)
    case EINum(n) => (INum(n), st)
    case EStr(str) => (Str(str), st)
    case EBool(b) => (Bool(b), st)
    case EUndef => (Undef, st)
    case ENull => (Null, st)
    case EAbsent => (Absent, st)
    case EMap(ty, props) =>
      val (addr, s0) = st.allocMap(ty)
      (addr, (s0 /: props) {
        case (st, (e1, e2)) =>
          val (k, s0) = interp(e1)(st)
          val (v, s1) = interp(e2)(s0)
          s1.updated(addr, k, v)
      })
    case EList(exprs) =>
      val (vs, s0) = ((List[Value](), st) /: exprs) {
        case ((vs, st), expr) =>
          val (v, s0) = interp(expr)(st)
          (v :: vs, s0)
      }
      s0.allocList(vs.reverse)
    case EPop(expr) =>
      val (v, s0) = interp(expr)(st)
      v match {
        case addr: Addr => s0.pop(addr)
        case v => error(s"not an address: $v")
      }
    case ERef(ref) =>
      val (refV, s0) = interp(ref)(st)
      (s0(refV), s0)
    case EFunc(params, body) =>
      (Func("<empty>", params, body), st)
    case EApp(fexpr, args) =>
      val (fv, s0) = interp(fexpr)(st)
      fv match {
        case Func(fname, params, body) =>
          val (locals, s1, _) = ((Map[Id, Value](), s0, args) /: params) {
            case ((map, st, arg :: rest), param) =>
              val (av, s0) = interp(arg)(st)
              (map + (param -> av), s0, rest)
            case (triple, _) => triple
          }
          val newSt = fixpoint(s1.copy(context = fname, insts = List(body), locals = locals))
          newSt.retValue match {
            case Some(v) => (v, s1.copy(heap = newSt.heap, globals = newSt.globals))
            case None => error(s"no return value")
          }
        case v => error(s"not a function: $v")
      }
    case ERun(expr, name, args) => {
      val (v, s0) = interp(expr)(st)
      v match {
        case ASTVal(ast) => {
          val (Func(fname, params, body), lst) = ast.semantics(name)
          val (nlst, s1) = ((lst.reverse, s0) /: args) {
            case ((lst, st), arg) =>
              val (av, s0) = interp(arg)(st)
              (av :: lst, s0)
          }
          val (locals, _) = ((Map[Id, Value](), nlst.reverse) /: params) {
            case ((map, arg :: rest), param) =>
              (map + (param -> arg), rest)
            case (pair, _) => pair
          }
          val newSt = fixpoint(s1.copy(context = fname, insts = List(body), locals = locals))
          newSt.retValue match {
            case Some(v) => (v, s1.copy(heap = newSt.heap, globals = newSt.globals))
            case None => error(s"no return value")
          }
        }
        case Str(s) if name == "StringToNumber" => (Num(s.toDouble), st)
        case v => error(s"not an AST value: $v")
      }
    }
    case EUOp(uop, expr) =>
      val (v, s0) = interp(expr)(st)
      (interp(uop)(v), s0)
    case EBOp(bop, left, right) =>
      val (lv, s0) = interp(left)(st)
      val (rv, s1) = interp(right)(s0)
      (interp(bop)(lv, rv), s1)
    case EExist(ref) =>
      val (refV, s0) = interp(ref)(st)
      (Bool(s0.contains(refV)), s0)
    case ETypeOf(expr) => {
      val (v, s0) = interp(expr)(st)
      (v match {
        case addr: Addr => Str(s0.heap.map.getOrElse(addr, error(s"unknown address: $addr")).ty.name)
        case Num(_) | INum(_) => Str("Number")
        case Str(_) => Str("String")
        case Bool(_) => Str("Boolean")
        case Undef => Str("Undefined")
        case Null => Str("Null")
        case Absent => Str("Absent")
        case Func(_, _, _) => Str("Function")
        case ASTVal(_) => Str("AST")
      }, s0)
    }
    case ENotYetImpl(msg) => error(s"[NotYetImpl]:${st.context}: $msg")
  }

  // references
  def interp(ref: Ref): State => (RefValue, State) = st => ref match {
    case RefId(id) => (RefValueId(id), st)
    case RefProp(ref, expr) =>
      val (refV, s0) = interp(ref)(st)
      s0(refV) match {
        case addr: Addr =>
          val (v, s1) = interp(expr)(s0)
          (RefValueProp(addr, v), s1)
        case v => error(s"not an address: $v")
      }
  }

  // unary operators
  def interp(uop: UOp): Value => Value = (uop, _) match {
    case (ONeg, Num(n)) => Num(-n)
    case (ONeg, INum(n)) => INum(-n)
    case (ONot, Bool(b)) => Bool(!b)
    case (OBNot, INum(n)) => INum(~n)
    case (_, value) => error(s"wrong type of value for the operator $uop: $value")
  }

  // binary operators
  def interp(bop: BOp): (Value, Value) => Value = (bop, _, _) match {
    // double operations
    case (OPlus, Num(l), Num(r)) => Num(l + r)
    case (OSub, Num(l), Num(r)) => Num(l - r)
    case (OMul, Num(l), Num(r)) => Num(l * r)
    case (ODiv, Num(l), Num(r)) => Num(l / r)
    case (OMod, Num(l), Num(r)) => Num(l % r)
    case (OLt, Num(l), Num(r)) => Bool(l < r)

    // string operations
    case (OPlus, Str(l), Str(r)) => Str(l + r)
    case (OLt, Str(l), Str(r)) => Bool(l < r)

    // long operations
    case (OPlus, INum(l), INum(r)) => INum(l + r)
    case (OSub, INum(l), INum(r)) => INum(l - r)
    case (OMul, INum(l), INum(r)) => INum(l * r)
    case (ODiv, INum(l), INum(r)) => INum(l / r)
    case (OMod, INum(l), INum(r)) => INum(l % r)
    case (OLt, INum(l), INum(r)) => Bool(l < r)
    case (OBAnd, INum(l), INum(r)) => INum(l & r)
    case (OBOr, INum(l), INum(r)) => INum(l | r)
    case (OBXOr, INum(l), INum(r)) => INum(l ^ r)
    case (OLShift, INum(l), INum(r)) => INum(l << r)
    case (OSRShift, INum(l), INum(r)) => INum(l >> r)
    case (OURShift, INum(l), INum(r)) => INum(l >>> r)

    // logical operations
    case (OAnd, Bool(l), Bool(r)) => Bool(l && r)
    case (OOr, Bool(l), Bool(r)) => Bool(l || r)

    // equality operations
    case (OEq, l, r) => Bool(l == r)
    case (_, lval, rval) => error(s"wrong type: $lval $bop $rval")
  }
}
