package kr.ac.kaist.ase.core

import kr.ac.kaist.ase.model.{ Parser => JSParser }
import org.apache.commons.text.StringEscapeUtils

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
          case addr: Addr =>
            println(addr)
            println(beautify(s0.heap(addr)))
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
      s0(refV)
    case EFunc(params, varparam, body) =>
      (Func("<empty>", params, varparam, body), st)
    case EApp(fexpr, args) =>
      val (fv, s0) = interp(fexpr)(st)
      fv match {
        case Func(fname, params, varparam, body) =>
          val (locals0, s1, restArg) = ((Map[Id, Value](), s0, args) /: params) {
            case ((map, st, arg :: rest), param) =>
              val (av, s0) = interp(arg)(st)
              (map + (param -> av), s0, rest)
            case (triple, _) => triple
          }
          val (locals1, s2) = varparam.map((param) => {
            val (av, s0) = interp(EList(restArg))(s1)
            (locals0 + (param -> av), s0)
          }).getOrElse((locals0, s1))

          val newSt = fixpoint(s2.copy(context = fname, insts = List(body), locals = locals1))
          newSt.retValue match {
            case Some(v) => (v, s2.copy(heap = newSt.heap, globals = newSt.globals))
            case None => error(s"no return value")
          }
        case ASTMethod(Func(fname, params, _, body), baseLocals) =>
          val (locals, s1, _) = ((baseLocals, s0, args) /: params) {
            case ((map, st, arg :: rest), param) =>
              val (av, s0) = interp(arg)(st)
              (map + (param -> av), s0, rest)
            case (triple, _) => triple
          }
          val newSt = Interp.fixpoint(s1.copy(context = fname, insts = List(body), locals = locals))
          newSt.retValue match {
            case Some(v) => (v, s1.copy(heap = newSt.heap, globals = newSt.globals))
            case None => error(s"no return value")
          }
        case v => error(s"not a function: $v")
      }
    case EUOp(uop, expr) =>
      val (v, s0) = interp(expr)(st)
      (interp(uop)(v), s0)

    // logical operations
    case EBOp(OAnd, left, right) => shortCircuit(OAnd, false, _ && _, left, right, st)
    case EBOp(OOr, left, right) => shortCircuit(OOr, true, _ || _, left, right, st)
    case EBOp(bop, left, right) =>
      val (lv, s0) = interp(left)(st)
      val (rv, s1) = interp(right)(s0)
      (interp(bop)(lv, rv), s1)
    case ETypeOf(expr) => {
      val (v, s0) = interp(expr)(st)
      (v match {
        case addr: Addr =>
          val name = s0.heap.map.getOrElse(addr, error(s"unknown address: $addr")).ty.name
          Str(if (name.endsWith("Object")) "Object" else name)
        case Num(_) | INum(_) => Str("Number")
        case Str(_) => Str("String")
        case Bool(_) => Str("Boolean")
        case Undef => Str("Undefined")
        case Null => Str("Null")
        case Absent => Str("Absent")
        case Func(_, _, _, _) => Str("Function")
        case ASTVal(_) => Str("AST")
        case ASTMethod(_, _) => Str("ASTMethod")
      }, s0)
    }
    case EIsInstanceOf(base, name) => interp(base)(st) match {
      case (ASTVal(ast), s0) => (Bool(ast.getNames contains name), s0)
      case v => error(s"not an AST value: $v")
    }
    case EGetSyntax(base) => interp(base)(st) match {
      case (ASTVal(ast), s0) => (Str(ast.toString), s0)
      case (v, s0) => error(s"not an AST value: $v")
    }
    case EParseSyntax(code, rule) => interp(code)(st) match {
      case (ASTVal(ast), s0) => JSParser.rules.get(rule) match {
        case Some(p) =>
          (ASTVal(JSParser.parseAll(JSParser.term("") ~> p(ast.parserParams), ast.toString).get), s0)
        case None => error(s"not exist parse rule: $rule")
      }
      case (Str(str), s0) => JSParser.rules.get(rule) match {
        case Some(p) =>
          (ASTVal(JSParser.parseAll(JSParser.term("") ~> p(Nil), str).get), s0)
        case None => error(s"not exist parse rule: $rule")
      }
      case (v, s0) => error(s"not an AST value or a string: $v")
    }
    case EParseString(code, pop) => interp(code)(st) match {
      case (Str(s), s0) => {
        (pop match {
          case PStr => Str(StringEscapeUtils.unescapeEcmaScript(s.substring(1, s.length - 1)))
          case PNum => Num(s.toDouble)
        }, s0)
      }
      case (v, s0) => error(s"not an String value: $v")
    }
    case EConvert(expr, cop) => interp(expr)(st) match {
      case (Str(s), s0) => {
        (cop match {
          case CStrToNum => Num(s.toDouble) // TODO : implement StrToNum to follow specification
          case _ => error(s"not convertable option: Str to $cop")
        }, s0)
      }
      case (INum(n), s0) => {
        (cop match {
          case CNumToStr => Str(Helper.toStringHelper(n))
          case _ => error(s"not convertable option: Num to $cop")
        }, s0)
      }
      case (Num(n), s0) => {
        (cop match {
          case CNumToStr => Str(Helper.toStringHelper(n))
          case _ => error(s"not convertable option: Num to $cop")
        }, s0)
      }
      case (v, s0) => error(s"not an convertable value: $v")
    }
    case EContains(list, elem) =>
      val (l, s0) = interp(list)(st)
      l match {
        case (addr: Addr) => s0.heap(addr) match {
          case CoreList(vs) =>
            val (v, s1) = interp(elem)(st)
            (Bool(vs contains v), s1)
          case obj => error(s"not a list: $obj")
        }
        case v => error(s"not an address: $v")
      }
    case ECopy(expr) =>
      val (v, s0) = interp(expr)(st)
      v match {
        case (addr: Addr) => s0.copyObj(addr)
        case v => error(s"not an address: $v")
      }
    case EKeys(expr) =>
      val (v, s0) = interp(expr)(st)
      v match {
        case (addr: Addr) => s0.keys(addr)
        case v => error(s"not an address: $v")
      }
    case ENotYetImpl(msg) => error(s"[NotYetImpl]:${st.context}: $msg")
  }

  // references
  def interp(ref: Ref): State => (RefValue, State) = st => ref match {
    case RefId(id) => (RefValueId(id), st)
    case RefProp(ref, expr) =>
      val (refV, s0) = interp(ref)(st)
      val (base, s1) = s0(refV)
      val (p, s2) = interp(expr)(s1)
      ((base, p) match {
        case (addr: Addr, p) => RefValueProp(addr, p)
        case (ast: ASTVal, Str(name)) => RefValueAST(ast, name)
        case v => error(s"not an address: $v")
      }, s2)
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

    // double with long operations
    case (OPlus, INum(l), Num(r)) => Num(l + r)
    case (OSub, INum(l), Num(r)) => Num(l - r)
    case (OMul, INum(l), Num(r)) => Num(l * r)
    case (ODiv, INum(l), Num(r)) => Num(l / r)
    case (OMod, INum(l), Num(r)) => Num(l % r)
    case (OLt, INum(l), Num(r)) => Bool(l < r)
    case (OPlus, Num(l), INum(r)) => Num(l + r)
    case (OSub, Num(l), INum(r)) => Num(l - r)
    case (OMul, Num(l), INum(r)) => Num(l * r)
    case (ODiv, Num(l), INum(r)) => Num(l / r)
    case (OMod, Num(l), INum(r)) => Num(l % r)
    case (OLt, Num(l), INum(r)) => Bool(l < r)

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
    case (OXor, Bool(l), Bool(r)) => Bool(l ^ r)

    // equality operations
    case (OEq, INum(l), Num(r)) => Bool(l == r)
    case (OEq, Num(l), INum(r)) => Bool(l == r)
    case (OEq, l, r) => Bool(l == r)

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

