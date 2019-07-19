package kr.ac.kaist.ase.core

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import kr.ac.kaist.ase.DEBUG_INTERP
import kr.ac.kaist.ase.error.NotSupported
import kr.ac.kaist.ase.model.{ Parser => ESParser, ESValueParser }
import org.apache.commons.text.StringEscapeUtils

// CORE Interpreter
class Interp {

  val timeout: Long = 3000
  val startTime: Long = System.currentTimeMillis
  var instCount = 0

  def apply(inst: Inst) = interp(inst)
  def apply(st: State) = fixpoint(st)

  // perform transition until instructions are empty
  def fixpoint(st: State): State = st.insts match {
    case Nil => st
    case inst :: rest =>
      fixpoint(interp(inst)(st.copy(insts = rest)))
  }

  // instructions
  def interp(inst: Inst): State => State = st => {
    instCount = instCount + 1
    if ((instCount % 10000 == 0) && (System.currentTimeMillis - startTime) > timeout) error("timeoutInst")
    if (DEBUG_INTERP) inst match {
      case ISeq(_) =>
      case _ => println(s"${st.context}: ${beautify(inst)}")
    }
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
      case IAppend(expr, list) =>
        val (exprV, s0) = interp(expr)(st)
        val (listV, s1) = interp(list)(s0)
        listV match {
          case addr: Addr => s0.append(addr, exprV)
          case v => error(s"not an address: $v")
        }
      case IPrepend(expr, list) =>
        val (exprV, s0) = interp(expr)(st)
        val (listV, s1) = interp(list)(s0)
        listV match {
          case addr: Addr => s0.prepend(addr, exprV)
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
    case ESymbol(desc) =>
      interp(desc)(st) match {
        case (Str(str), st) => st.allocSymbol(str)
        case (v, _) => error(s"not a string: $v")
      }
    case EPop(list, idx) =>
      val (l, s0) = interp(list)(st)
      val (k, s1) = interp(idx)(s0)
      l match {
        case addr: Addr => s1.pop(addr, k)
        case v => error(s"not an address: $v")
      }
    case ERef(ref) =>
      val (refV, s0) = interp(ref)(st)
      interp(refV)(s0)
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
          (newSt.retValue.getOrElse(Absent), s2.copy(heap = newSt.heap, globals = newSt.globals))
        case ASTMethod(Func(fname, params, _, body), baseLocals) =>
          val (locals, s1, _) = ((baseLocals, s0, args) /: params) {
            case ((map, st, arg :: rest), param) =>
              val (av, s0) = interp(arg)(st)
              (map + (param -> av), s0, rest)
            case (triple, _) => triple
          }
          val newSt = fixpoint(s1.copy(context = fname, insts = List(body), locals = locals))
          (newSt.retValue.getOrElse(Absent), s1.copy(heap = newSt.heap, globals = newSt.globals))
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
      (Str(v match {
        case addr: Addr => s0.heap.map.getOrElse(addr, error(s"unknown address: $addr")) match {
          case CoreNotSupported(name) => throw NotSupported(name)
          case obj => obj.ty.name
        }
        case Num(_) | INum(_) => "Number"
        case Str(_) => "String"
        case Bool(_) => "Boolean"
        case Undef => "Undefined"
        case Null => "Null"
        case Absent => "Absent"
        case Func(_, _, _, _) => "Function"
        case ASTVal(_) => "AST"
        case ASTMethod(_, _) => "ASTMethod"
      }), s0)
    }
    case EIsInstanceOf(base, kind) => interp(base)(st) match {
      case (ASTVal(ast), s0) => (Bool(ast.getKinds contains kind), s0)
      case (v, _) => error(s"not an AST value: $v")
    }
    case EGetElems(base, kind) => interp(base)(st) match {
      case (ASTVal(ast), s0) => s0.allocList(ast.getElems(kind).map(ASTVal(_)))
      case (v, _) => error(s"not an AST value: $v")
    }
    case EGetSyntax(base) => interp(base)(st) match {
      case (ASTVal(ast), s0) => (Str(ast.toString), s0)
      case (v, s0) => error(s"not an AST value: $v")
    }
    case EParseSyntax(code, rule, flags) =>
      val (v, s0) = interp(code)(st)
      val (p, s1) = interp(rule)(st) match {
        case (Str(str), st) => (ESParser.rules.getOrElse(str, error(s"not exist parse rule: $rule")), st)
        case (v, _) => error(s"not a string: $v")
      }
      v match {
        case ASTVal(ast) =>
          val newAst = try {
            Await.result(Future(
              ESParser.parse(p(ast.parserParams), ast.toString).get
            ), timeout.milliseconds)
          } catch {
            case e: TimeoutException => error("parser timeout")
          }
          if (newAst.exists(x => x.startsWith("Async") || x.startsWith("Generator"))) throw NotSupported("Async/Generator")
          (ASTVal(newAst), s1)
        case Str(str) =>
          val (s2, parserParams) = ((s1, List[Boolean]()) /: flags) {
            case ((st, ps), param) =>
              val (av, s1) = interp(param)(st)
              av match {
                case Bool(v) => (s1, ps :+ v)
                case _ => error(s"parserParams should be boolean")
              }
          }
          val ast = try {
            Await.result(Future(
              ESParser.parse(p(parserParams), str).get
            ), timeout.milliseconds)
          } catch {
            case e: TimeoutException => error("parser timeout")
          }
          if (ast.exists(x => x.startsWith("Async") || x.startsWith("Generator"))) throw NotSupported("Async/Generator")
          (ASTVal(ast), s2)
        case v => error(s"not an AST value or a string: $v")
      }
    case EParseString(code, pop) => interp(code)(st) match {
      case (Str(s), s0) => (pop match {
        case PStr => Str(ESValueParser.parseString(s))
        case PNum => Num(ESValueParser.parseNumber(s))
        case PTVNoSubs => Str(ESValueParser.parseTVNoSubstitutionTemplate(s))
        case PTRVNoSubs => Str(ESValueParser.parseTRVNoSubstitutionTemplate(s))
        case PTVHead => Str(ESValueParser.parseTVTemplateHead(s))
        case PTRVHead => Str(ESValueParser.parseTRVTemplateHead(s))
        case PTVMiddle => Str(ESValueParser.parseTVTemplateMiddle(s))
        case PTRVMiddle => Str(ESValueParser.parseTRVTemplateMiddle(s))
        case PTVTail => Str(ESValueParser.parseTVTemplateTail(s))
        case PTRVTail => Str(ESValueParser.parseTRVTemplateTail(s))
      }, s0)
      case (v, s0) => error(s"not an String value: $v")
    }
    case EConvert(expr, cop) => interp(expr)(st) match {
      case (Str(s), s0) => {
        (cop match {
          case CStrToNum => Num(ESValueParser.str2num(s))
          case _ => error(s"not convertable option: Str to $cop")
        }, s0)
      }
      case (INum(n), s0) => {
        (cop match {
          case CNumToStr => Str(Helper.toStringHelper(n))
          case CNumToInt => INum(n)
          case _ => error(s"not convertable option: Num to $cop")
        }, s0)
      }
      case (Num(n), s0) => {
        (cop match {
          case CNumToStr => Str(Helper.toStringHelper(n))
          case CNumToInt => INum((math.signum(n) * math.floor(math.abs(n))).toLong)
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
    case ENotSupported(msg) => throw NotSupported(msg)
  }

  // references
  def interp(ref: Ref): State => (RefValue, State) = st => ref match {
    case RefId(id) => (RefValueId(id), st)
    case RefProp(ref, expr) =>
      val (refV, s0) = interp(ref)(st)
      val (base, s1) = interp(refV)(s0)
      val (p, s2) = interp(expr)(s1)
      ((base, p) match {
        case (addr: Addr, p) => RefValueProp(addr, p)
        case (ast: ASTVal, Str(name)) => RefValueAST(ast, name)
        case (Str(str), p) => RefValueString(str, p)
        case v => error(s"not an address: $v")
      }, s2)
  }

  def interp(refV: RefValue): State => (Value, State) = st => refV match {
    case RefValueId(id) =>
      (st.locals.getOrElse(id, st.globals.getOrElse(id, Absent)), st)
    case RefValueProp(addr, value) =>
      (st.heap(addr, value), st)
    case RefValueAST(astV, name) =>
      val ASTVal(ast) = astV
      ast.semantics(name) match {
        case Some((Func(fname, params, varparam, body), lst)) =>
          val (locals, rest) = ((Map[Id, Value](), params) /: (astV :: lst)) {
            case ((map, param :: rest), arg) =>
              (map + (param -> arg), rest)
            case (pair, _) => pair
          }
          rest match {
            case Nil =>
              val newSt = fixpoint(st.copy(context = fname, insts = List(body), locals = locals))
              (newSt.retValue.getOrElse(Absent), st.copy(heap = newSt.heap, globals = newSt.globals))
            case _ =>
              (ASTMethod(Func(fname, rest, varparam, body), locals), st)
          }
        case None => ast.subs(name) match {
          case Some(v) => (v, st)
          case None => error(s"Unexpected semantics: ${ast.name}.$name")
        }
      }
    case RefValueString(str, value) => value match {
      case Str("length") => (INum(str.length), st)
      case INum(k) => (Str(str(k.toInt).toString), st)
      case v => error(s"wrong access of string reference: $str.$value")
    }
  }

  // unary operators
  def interp(uop: UOp): Value => Value = (uop, _) match {
    case (ONeg, Num(n)) => Num(-n)
    case (ONeg, INum(n)) => INum(-n)
    case (ONot, Bool(b)) => Bool(!b)
    case (OBNot, Num(n)) => INum(~(n.toInt))
    case (OBNot, INum(n)) => INum(~n)
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
    case (OSub, Str(l), INum(r)) => Str(l.dropRight(r.toInt))
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
    case (OLShift, INum(l), INum(r)) => INum((l.toInt << r.toInt).toLong)
    case (OSRShift, INum(l), INum(r)) => INum((l.toInt >> r.toInt).toLong)
    case (OURShift, INum(l), INum(r)) => INum((l.toInt >>> r.toInt).toLong)

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

