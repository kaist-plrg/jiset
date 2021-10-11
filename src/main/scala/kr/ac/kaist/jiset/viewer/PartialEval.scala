package kr.ac.kaist.jiset.viewer

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.StateMonad
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec
import scala.collection.mutable.{ Map => MMap }
import kr.ac.kaist.jiset.js.ast.AbsAST
import scala.util.DynamicVariable
import kr.ac.kaist.jiset.js.ast.Lexical
import kr.ac.kaist.jiset.js.Initialize

case class PartialContext(state: PartialState, fmap: Map[(Algo, List[PartialValue]), Option[(Algo, PartialValue)]])

case class PartialState(locals: Map[String, PartialValue], ret: PartialValue) {
  def merge(other: PartialState): ((List[(String, StaticExpr)], List[(String, StaticExpr)]), PartialState) = {
    if (this.ret == other.ret && this.ret.isInstanceOf[StaticValue]) {
      ((List(), List()), this)
    } else {
      ((List(), List()), this.copy(ret = DV))
    }
  }
}

sealed trait AccessPath
case object ANone extends AccessPath
case class ALocal(id: String) extends AccessPath
case class AMapProp(p: AccessPath, s: Option[String]) extends AccessPath
case class AListIdx(p: AccessPath, s: Option[Int]) extends AccessPath

sealed trait PartialObj

sealed trait PartialValue
case object DV extends PartialValue
case class StaticOwnedAddr(owner: AccessPath, content: PartialObj) extends PartialValue
case class StaticValue(v: Value) extends PartialValue

sealed trait PartialExpr {
  def toExpr: Expr
  def toPV: PartialValue
}
case class DynamicExpr(e: Expr) extends PartialExpr {
  def toExpr: Expr = e
  def toPV: PartialValue = DV
}
case class StaticExpr(v: Value) extends PartialExpr {
  def toExpr: Expr = v match {
    case CompValue(ty, value, targetOpt) => ???
    case Const(name) => ???
    case NamedAddr(name) => ???
    case DynamicAddr(long) => ???
    case Func(algo) => ERef(RefId(Initialize.initGlobal.find { case (_, v2) => v2 == v }.get._1))
    case Clo(ctxtName, params, locals, cursorOpt) => ???
    case Cont(params, context, ctxtStack) => ???
    case ASTVal(ast) => ???
    case Num(double) => ENum(double)
    case INum(long) => EINum(long)
    case BigINum(b) => EBigINum(b)
    case Str(str) => EStr(str)
    case Bool(bool) => EBool(bool)
    case Undef => EUndef
    case Null => ENull
    case Absent => EAbsent
  }
  def toPV: PartialValue = StaticValue(v)
}

sealed trait ParamHeap

object PartialStateMonad extends StateMonad[PartialContext]

// partial evaluator for IR functions with a given syntactic view
object PartialEval {

  def getLocals(params: List[Param], args: List[PartialValue]): MMap[String, PartialValue] = {
    val map = MMap[String, PartialValue]()
    @tailrec
    def aux(ps: List[Param], as: List[PartialValue]): Unit = (ps, as) match {
      case (Nil, Nil) =>
      case (Param(name, kind) :: pl, Nil) => kind match {
        case Param.Kind.Normal => error(s"remaining parameter: $name")
        case _ => {
          map += name -> StaticValue(Absent)
          aux(pl, Nil)
        }
      }
      case (Nil, args) => {
        val argsStr = args.mkString("[", ", ", "]")
        error(s"remaining arguments: $argsStr")
      }
      case (param :: pl, arg :: al) => {
        map += param.name -> arg
        aux(pl, al)
      }
    }
    aux(params, args)
    map
  }

  def toPV(v: Value): PartialValue = v match {
    case ASTVal(ast: AbsAST) => DV
    case _ => StaticValue(v)
  }

  def apply(view: SyntacticView): Algo = {
    val (targetAlgo, asts) = view.ast.semantics("Evaluation").get
    val ((nalgo, _), _) = pe(targetAlgo, asts.map(toPV))(PartialContext(PartialState(Map(), DV), Map()))
    nalgo
  }

  def pe(algo: Algo, args: List[PartialValue]): PartialStateMonad.Result[(Algo, PartialValue)] = (dcontext: PartialContext) => {
    dcontext.fmap.get((algo, args)) match {
      case None => {
        val locals = getLocals(algo.head.params, args)
        val (newInsts, ncontext) = pe(algo.rawBody)(PartialContext(PartialState(locals.toMap, DV), dcontext.fmap + ((algo, args) -> None)))
        val nalgo = new Algo(algo.head, algo.id, newInsts, algo.code)
        ((nalgo, ncontext.state.ret), PartialContext(dcontext.state, ncontext.fmap + ((algo, args) -> Some((nalgo, ncontext.state.ret)))))
      }

      case Some(None) => ((algo, DV), dcontext)
      case Some(Some((a, p))) => ((a, p), dcontext)
    }
  }

  def pe(inst: Inst): PartialStateMonad.Result[Inst] = (dcontext: PartialContext) => {
    inst match {
      case ISeq(insts) => {
        val (z, y) = insts.foldLeft((List[Inst](), dcontext)) {
          case ((li, dc), i) => if (dc.state.ret.isInstanceOf[StaticValue]) (li, dc) else {
            val (i2, dc2) = pe(i)(dc)
            (li :+ i2, dc2)
          }
        }
        (ISeq(z), y)
      }
      case IAccess(id, bexpr, expr, args) => {
        var (baseE, dcontext1) = pe(bexpr)(dcontext)
        val (propE, dcontext2) = pe(expr)(dcontext1)
        val (argsE, dcontext3) = args.foldLeft((List[PartialExpr](), dcontext2)) {
          case ((l, dc), a) => { val (x, y) = pe(a)(dc); (l :+ x, y) }
        }
        (baseE, propE) match {
          case (StaticExpr(b), StaticExpr(p)) =>
            {
              val escapedb = b match {
                case NormalComp(v) => Some(v)
                case CompValue(_, _, _) => None
                case p: PureValue => Some(p)
              }
              val escapedp = p match {
                case NormalComp(v) => Some(v)
                case CompValue(_, _, _) => None
                case p: PureValue => Some(p)
              }
              (escapedb, escapedp) match {
                case (Some(eb), Some(ep)) => (eb, ep) match {

                  case (ASTVal(Lexical(kind, str)), Str(name)) =>
                    (IExpr(EStr("skip")), dcontext3.copy(state = dcontext3.state.copy(
                      locals = dcontext3.state.locals + (id.name -> StaticValue(Interp.getLexicalValue(kind, name, str)))
                    )))
                  case (ASTVal(ast), Str("parent")) => (IAccess(id, baseE.toExpr, propE.toExpr, argsE.map(_.toExpr)), dcontext3)
                  case (ASTVal(ast), Str("children")) => (IAccess(id, baseE.toExpr, propE.toExpr, argsE.map(_.toExpr)), dcontext3)
                  case (ASTVal(ast), Str("kind")) => (IAccess(id, baseE.toExpr, propE.toExpr, argsE.map(_.toExpr)), dcontext3)
                  case (ASTVal(ast), Str(name)) => ast.semantics(name) match {
                    case Some((algo, asts)) => {
                      val args = asts.map(toPV) ++ argsE.map(_.toPV)
                      val ((nalgo, rpv), dcontext4) = pe(algo, args)(dcontext3)
                      rpv match {
                        case v: StaticValue => (IExpr(EStr("skip")), dcontext4.copy(state = dcontext4.state.copy(
                          locals = dcontext4.state.locals + (id.name -> v)
                        )))
                        case _ =>
                          if (args.forall((p) => p == DV)) (IAccess(id, baseE.toExpr, propE.toExpr, argsE.map(_.toExpr)), dcontext4) else (IApp(id, EStr(algo.id + args.toString), List()), dcontext4)
                      }
                    }
                    case _ => (IAccess(id, baseE.toExpr, propE.toExpr, argsE.map(_.toExpr)), dcontext3)
                  }
                  case _ => (IAccess(id, baseE.toExpr, propE.toExpr, argsE.map(_.toExpr)), dcontext3)
                }
                case _ => (IAccess(id, baseE.toExpr, propE.toExpr, argsE.map(_.toExpr)), dcontext3)
              }
            }
          case _ => {
            (IAccess(id, baseE.toExpr, propE.toExpr, argsE.map(_.toExpr)), dcontext3)
          }
        }
      }
      case IApp(id, fexpr, args) => {
        val (fpe, dcontext1) = pe(fexpr)(dcontext)
        val (argse, dcontext2) = args.foldLeft((List[PartialExpr](), dcontext1)) {
          case ((pel, ct), a) => { val (ae, ct2) = pe(a)(ct); (pel :+ ae, ct2) }
        }
        fpe match {
          case StaticExpr(Func(algo)) => {
            val argsv = argse.map(_.toPV)
            val ((nalgo, rpv), dcontext3) = pe(algo, argsv)(dcontext2)
            rpv match {
              case v: StaticValue => (IExpr(EStr("skip")), dcontext3.copy(state = dcontext3.state.copy(
                locals = dcontext3.state.locals + (id.name -> v)
              )))
              case _ =>
                if (argsv.forall((p) => p == DV)) (IApp(id, fpe.toExpr, argse.map(_.toExpr)), dcontext3) else (IApp(id, EStr(algo.id + argsv.toString), List()), dcontext3)

            }
          }
          case _ => (IApp(id, fpe.toExpr, argse.map(_.toExpr)), dcontext2)
        }
      }
      case IAppend(expr, list) => (inst, dcontext) // TODO
      case IAssert(expr) => (inst, dcontext) // TODO
      case IAssign(ref, expr) => (inst, dcontext) // TODO
      case IClo(id, params, captured, body) => (inst, dcontext) // TODO
      case ICont(id, params, body) => (inst, dcontext) // TODO
      case IDelete(ref) => (inst, dcontext) // TODO
      case IExpr(expr) => pe(expr)(dcontext) match {
        case (e, dcontext1) => (IExpr(e.toExpr), dcontext1)
      }
      case IIf(cond, thenInst, elseInst) => pe(cond)(dcontext) match {
        case (condE, dcontext1) => {
          condE match {
            case StaticExpr(Bool(true)) => {
              val (thenInstI, dcontext2) = pe(thenInst)(dcontext1)
              (thenInstI, dcontext2)
            }
            case StaticExpr(Bool(false)) =>
              {
                val (elseInstI, dcontext2) = pe(elseInst)(dcontext1)
                (elseInstI, dcontext2)
              }
            case _ => {
              val (thenInstI, PartialContext(nstate1, fmap1)) = pe(thenInst)(dcontext1)
              val (elseInstI, PartialContext(nstate2, fmap2)) = pe(elseInst)(PartialContext(dcontext.state, fmap1))
              val ((eif, efalse), nstate) = nstate1 merge nstate2
              (IIf(
                condE.toExpr,
                ISeq(eif.map { case (x, v) => ILet(Id(x), v.toExpr) } :+ thenInstI),
                ISeq(efalse.map { case (x, v) => ILet(Id(x), v.toExpr) } :+ elseInstI)
              ), PartialContext(nstate, fmap2))
            }
          }
        }
      }
      case ILet(id, expr) => pe(expr)(dcontext) match {
        case (DynamicExpr(e), dcontext1) => (ILet(id, e), dcontext1.copy(state = dcontext1.state.copy(locals = dcontext1.state.locals + (id.name -> DV))))
        case (StaticExpr(value), dcontext1) => (IExpr(EStr("skip")), dcontext1.copy(state = dcontext1.state.copy(locals = dcontext1.state.locals + (id.name -> StaticValue(value)))))
      }
      case IPrepend(expr, list) => (inst, dcontext) // TODO
      case IPrint(expr) => (inst, dcontext) // TODO
      case IReturn(expr) => {
        val (ne, dcontext1) = pe(expr)(dcontext)
        (IReturn(ne.toExpr), dcontext1.copy(state = dcontext1.state.copy(ret = ne.toPV)))
      }
      case IThrow(name) => (inst, dcontext) // TODO
      case IWhile(cond, body) => (inst, dcontext) // TODO
      case IWithCont(id, params, body) => (inst, dcontext) // TODO
    }
  }

  def pe(expr: Expr): PartialStateMonad.Result[PartialExpr] = (dcontext: PartialContext) => expr match {
    case ENum(n) => (StaticExpr(Num(n)), dcontext)
    case EINum(n) => (StaticExpr(INum(n)), dcontext)
    case EBigINum(b) => (StaticExpr(BigINum(b)), dcontext)
    case EStr(str) => (StaticExpr(Str(str)), dcontext)
    case EBool(b) => (StaticExpr(Bool(b)), dcontext)
    case EUndef => (StaticExpr(Undef), dcontext)
    case ENull => (StaticExpr(Null), dcontext)
    case EAbsent => (StaticExpr(Absent), dcontext)
    case EConst(name) => (DynamicExpr(expr), dcontext) // TODO
    case EComp(ty, value, target) => (DynamicExpr(expr), dcontext) // TODO
    case EMap(ty, props) => (DynamicExpr(expr), dcontext) // TODO
    case EList(exprs) => (DynamicExpr(expr), dcontext) // TODO
    case ESymbol(desc) => (DynamicExpr(expr), dcontext) // TODO
    case EPop(list, idx) => (DynamicExpr(expr), dcontext) // TODO
    case ERef(ref) => ref match {
      case RefId(id) => dcontext.state.locals.get(id.name) match {
        case Some(StaticValue(ASTVal(ast: AbsAST))) => (DynamicExpr(expr), dcontext)
        case Some(StaticValue(v)) => (StaticExpr(v), dcontext)
        case Some(_) => (DynamicExpr(expr), dcontext)
        case None => Initialize.initGlobal.get(id) match {
          case Some(v: Func) => (StaticExpr(v), dcontext)
          case _ => (DynamicExpr(expr), dcontext)
        }
      }
      case RefProp(ref, expr) => (DynamicExpr(expr), dcontext) // TODO: getRefProp
    }
    case EUOp(uop, expr) => {
      val (e, dcontext1) = pe(expr)(dcontext)
      e match {
        case DynamicExpr(e) => (DynamicExpr(EUOp(uop, e)), dcontext1)
        case StaticExpr(v) => (StaticExpr(Interp.interp(uop, v)), dcontext1)
      }
    }
    case EBOp(bop, left, right) => {
      val (le, dcontext1) = pe(left)(dcontext)
      val (re, dcontext2) = pe(right)(dcontext1)
      (le, re) match {
        case (StaticExpr(lv), StaticExpr(rv)) => (StaticExpr(Interp.interp(bop, lv, rv)), dcontext2)
        case (_, _) => (DynamicExpr(EBOp(bop, le.toExpr, re.toExpr)), dcontext2)
      }
    }
    case ETypeOf(expr) => {
      val (ne, dcontext1) = pe(expr)(dcontext)
      ne match {
        case DynamicExpr(e) => (DynamicExpr(ETypeOf(ne.toExpr)), dcontext1)
        case StaticExpr(v) => (DynamicExpr(ETypeOf(ne.toExpr)), dcontext) // TODO
      }
    }
    case EIsCompletion(expr) => (DynamicExpr(EIsCompletion(expr)), dcontext) // TODO
    case EIsInstanceOf(base, name) => {
      val (be, dcontext1) = pe(base)(dcontext)
      be match {
        case DynamicExpr(_) => (DynamicExpr(be.toExpr), dcontext1)
        case StaticExpr(bv) => {
          val b = if (bv.isAbruptCompletion) StaticExpr(Bool(false))
          else bv.escaped match {
            case ASTVal(ast) => StaticExpr(Bool(ast.name == name || ast.getKinds.contains(name)))
            case Str(str) => StaticExpr(Bool(str == name))
            case addr: Addr => DynamicExpr(be.toExpr)
            case _ => StaticExpr(Bool(false))
          }
          (b, dcontext)
        }
      }
    }
    case EGetElems(base, name) => (DynamicExpr(expr), dcontext) // TODO
    case EGetSyntax(base) => (DynamicExpr(expr), dcontext) // TODO
    case EParseSyntax(code, rule, parserParams) => (DynamicExpr(expr), dcontext) // TODO
    case EConvert(source, target, flags) => (DynamicExpr(expr), dcontext) // TODO
    case EContains(list, elem) => (DynamicExpr(expr), dcontext) // TODO
    case EReturnIfAbrupt(expr, check) => {
      val (ne, dcontext1) = pe(expr)(dcontext)
      ne match {
        case DynamicExpr(e) => (DynamicExpr(EReturnIfAbrupt(e, check)), dcontext1)
        case StaticExpr(v) => {
          v match {
            case NormalComp(value) => (StaticExpr(value), dcontext1)
            case pure: PureValue => (StaticExpr(pure), dcontext1)
            case _ => (DynamicExpr(EReturnIfAbrupt(ne.toExpr, check)), dcontext1)
          }
        }
      }
    }
    case ECopy(obj) => (DynamicExpr(expr), dcontext) // TODO
    case EKeys(mobj, intSorted) => (DynamicExpr(expr), dcontext) // TODO
    case ENotSupported(msg) => (DynamicExpr(expr), dcontext) // TODO
  }

}
