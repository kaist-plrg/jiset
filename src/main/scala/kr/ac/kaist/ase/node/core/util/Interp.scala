package kr.ac.kaist.ase.node.core

// CORE Interpreter
object Interp {
  // perform transition until instructions are empty
  def fixpoint(st: State): State = st.insts match {
    case Nil => st
    case inst :: rest =>
      fixpoint(interp(inst)(st.copy(insts = rest)))
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Syntax
  ////////////////////////////////////////////////////////////////////////////////

  // instructions
  def interp(inst: Inst): State => State = st => {
    inst match {
      case IExpr(lhs, expr) =>
        val prop = interp(lhs)(st)
        val value = interp(expr)(st)
        st.updated(prop, value)
      case IAlloc(lhs, ty) =>
        val prop = interp(lhs)(st)
        val (newAddr, newSt) = st.alloc(ty)
        newSt.updated(prop, newAddr)
      case IDelete(ref) =>
        val prop = interp(ref)(st)
        st.deleted(prop)
      case IApp(lhs, fun, args) =>
        val prop = interp(lhs)(st)
        interp(fun)(st) match {
          case Func(params, body) =>
            val (idMap, _) = ((Map[Id, Value](), args) /: params) {
              case ((map, Nil), param) =>
                (map + (param -> Undef), Nil)
              case ((map, arg :: rest), param) =>
                (map + (param -> interp(arg)(st)), rest)
            }
            val (locals, newSt) = st.allocLocals(idMap)
            val newEnv = st.env.copy(
              locals = locals,
              retCont = Some(Cont(prop, st.insts, st.env))
            )
            val retInst = IReturn(EUndef)
            newSt.copy(insts = List(body, retInst), env = newEnv)
          case v => error(s"not a function: $v")
        }
      case IReturn(expr) =>
        val value = interp(expr)(st)
        st.env.retCont match {
          case Some(cont) => st.continue(cont, value)
          case None => error(s"unvailable return: $value")
        }
      case IIf(cond, thenInst, elseInst) => interp(cond)(st) match {
        case Bool(true) => st.copy(insts = thenInst :: st.insts)
        case Bool(false) => st.copy(insts = elseInst :: st.insts)
        case v => error(s"not a boolean: $v")
      }
      case IWhile(cond, body) => interp(cond)(st) match {
        case Bool(true) => st.copy(insts = body :: inst :: st.insts)
        case Bool(false) => st
        case v => error(s"not a boolean: $v")
      }
      case ITry(lhs, tryInst) =>
        val prop = interp(lhs)(st)
        val newEnv = st.env.copy(excCont = Some(Cont(prop, st.insts, st.env)))
        val excInst = IThrow(EUndef)
        st.copy(insts = List(tryInst, excInst), env = newEnv)
      case IThrow(expr) =>
        val value = interp(expr)(st)
        st.env.excCont match {
          case Some(cont) => st.continue(cont, value)
          case None => error(s"uncaught exception: $value")
        }
      case ISeq(newInsts) => st.copy(insts = newInsts ++ st.insts)
      case IAssert(expr) => interp(expr)(st) match {
        case Bool(true) => st
        case Bool(false) => error(s"assertion failure: $expr")
        case v => error(s"not a boolean: $v")
      }
      case IPrint(expr) =>
        interp(expr)(st) match {
          case addr: Addr => println(beautify(st.heap(addr)))
          case v => println(beautify(v))
        }
        st
      case INotYetImpl(msg) => error(s"[NotYetImpl] $msg")
    }
  }

  // expresssions
  def interp(expr: Expr): State => Value = st => st match {
    case State(_, _, env, heap) => expr match {
      case ENum(n) => Num(n)
      case EINum(n) => INum(n)
      case EStr(str) => Str(str)
      case EBool(b) => Bool(b)
      case EUndef => Undef
      case ENull => Null
      case ERef(ref) =>
        val prop = interp(ref)(st)
        st(prop)
      case EFunc(params, body) =>
        Func(params, body)
      case EUOp(uop, expr) =>
        val v = interp(expr)(st)
        interp(uop)(v)
      case EBOp(bop, left, right) =>
        val lv = interp(left)(st)
        val rv = interp(right)(st)
        interp(bop)(lv, rv)
      case EExist(ref) =>
        val prop = interp(ref)(st)
        Bool(st.contains(prop))
      case ETypeOf(expr) => interp(expr)(st) match {
        case addr: Addr => Str(heap.map.getOrElse(addr, error(s"unknown address: $addr")).ty.name)
        case Num(_) | INum(_) => Str("Number")
        case Str(_) => Str("String")
        case Bool(_) => Str("Boolean")
        case Undef => Str("Undefined")
        case Null => Str("Null")
        case Func(_, _) => Str("Function")
      }
    }
  }

  // references
  def interp(ref: Ref): State => Prop = st => ref match {
    case RefId(id) =>
      val localId = PropId(st.env.locals, id)
      val globalId = GlobalId(id)
      if (st.contains(localId)) localId
      else if (st.contains(globalId)) globalId
      else error(s"free identifier: $id")
    case RefIdProp(ref, id) =>
      val prop = interp(ref)(st)
      st(prop) match {
        case addr: Addr => PropId(addr, id)
        case v => error(s"not an address: $v")
      }
    case RefStrProp(ref, expr) =>
      val prop = interp(ref)(st)
      st(prop) match {
        case addr: Addr => interp(expr)(st) match {
          case Str(str) => PropStr(addr, str)
          case INum(long) => PropStr(addr, long.toString)
          case v => error(s"not a string: $v")
        }
        case v => error(s"not an address: $v")
      }
  }

  // left-hand-sides
  def interp(lhs: Lhs): State => Prop = st => lhs match {
    case LhsRef(ref) => interp(ref)(st)
    case LhsLet(id) => PropId(st.env.locals, id)
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
