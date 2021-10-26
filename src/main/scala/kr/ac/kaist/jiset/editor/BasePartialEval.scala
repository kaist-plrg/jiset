
package kr.ac.kaist.jiset.editor

import kr.ac.kaist.jiset.spec.algorithm._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.util.StateMonad
import kr.ac.kaist.jiset.util._
import kr.ac.kaist.jiset.util.Useful._
import scala.annotation.tailrec
import scala.collection.mutable.{ Map => MMap }
import kr.ac.kaist.jiset.js.ast.AbsAST
import kr.ac.kaist.jiset.js.ast.Lexical
import kr.ac.kaist.jiset.js.Initialize
import kr.ac.kaist.jiset.analyzer.domain.FlatElem
import kr.ac.kaist.jiset.analyzer.domain.FlatTop
import kr.ac.kaist.jiset.analyzer.domain.FlatBot

class BasePartialEval extends PartialEval[EnvOnlyAbstraction[FlatVE]] {
  val vtbuilder = FlatVEBuilder
  val asbuilder = EnvOnlyAbstractionBuilder[FlatVE]()(vtbuilder)
  val instTransformer = new BaseInstTransformer(ExprTransformer(new BaseExprEvaluator))

}

class BaseInstTransformer(override val pet: ExprTransformer[EnvOnlyAbstraction[FlatVE]]) extends PassingInstTransformer(pet) {

  override def pe_iif: IIf => Result[Inst] = {

    case IIf(cond, thenInst, elseInst) => (dcontext: EnvOnlyAbstraction[FlatVE]) => {
      val ((v, ce), dcontext1) = pet.pe(cond)(dcontext)
      val (thenInstI, dcontext2) = pe(thenInst)(dcontext1)
      val (elseInstI, dcontext3) = pe(elseInst)(dcontext1)
      val nstate = dcontext2 join dcontext3
      v match {
        case FlatVE(FlatElem((Bool(true), _))) => (thenInstI, dcontext2)
        case FlatVE(FlatElem((Bool(false), _))) => (elseInstI, dcontext3)
        case _ => (IIf(
          ce,
          thenInstI,
          elseInstI
        ), nstate)
      }
    }
  }

  override def pe_iwhile: IWhile => Result[Inst] = {
    case IWhile(cond, body) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val ((v, ce), s2) = pet.pe(cond)(s)
      (v match {
        case FlatVE(FlatElem((Bool(false), _))) => (IExpr(EStr("empty")), s2)
        case _ => {
          def aux(dccurrent: EnvOnlyAbstraction[FlatVE]): EnvOnlyAbstraction[FlatVE] = {
            val (_, dc2) = pe(body)(dccurrent)
            val nc = dccurrent join dc2
            if (dccurrent == nc) nc
            else {
              // println(dccurrent.labelwiseContext)
              // println(dc3.labelwiseContext)
              aux(nc)
            }
          }
          val s2 = aux(s)
          val (b, s3) = pe(body)(s2)
          assert(s2 == (s3 join s2))
          val ((_, c2), _) = pet.pe(cond)(s3)
          (IWhile(c2, b), s3)
        }
      })
    }
  }
}

class BaseExprEvaluator extends TopExprEvaluator {

  val dynamicGlobal: Set[String] = {
    import kr.ac.kaist.jiset.js._
    Set(
      CONTEXT,
      EXECUTION_STACK,
      GLOBAL,
      JOB_QUEUE,
      PRIMITIVE,
      RET_CONT,
      SYMBOL_REGISTRY
    )
  }

  def isPermittedGlobal(id: Id) = if (dynamicGlobal contains id.name) false else true

  override def pe_enum: ENum => Result[FlatVE] = {
    case ENum(n) => (s: EnvOnlyAbstraction[FlatVE]) => (FlatVEBuilder.simple(Num(n)), s)
  }

  override def pe_einum: EINum => Result[FlatVE] = {
    case EINum(n) => (s: EnvOnlyAbstraction[FlatVE]) => (FlatVEBuilder.simple(INum(n)), s)
  }

  override def pe_ebiginum: EBigINum => Result[FlatVE] = {
    case EBigINum(b) => (s: EnvOnlyAbstraction[FlatVE]) => (FlatVEBuilder.simple(BigINum(b)), s)
  }

  override def pe_estr: EStr => Result[FlatVE] = {
    case EStr(str) => (s: EnvOnlyAbstraction[FlatVE]) => (FlatVEBuilder.simple(Str(str)), s)
  }

  override def pe_ebool: EBool => Result[FlatVE] = {
    case EBool(b) => (s: EnvOnlyAbstraction[FlatVE]) => (FlatVEBuilder.simple(Bool(b)), s)
  }

  override def pe_eundef: EUndef.type => Result[FlatVE] = {
    case EUndef => (s: EnvOnlyAbstraction[FlatVE]) => (FlatVEBuilder.simple(Undef), s)
  }

  override def pe_enull: ENull.type => Result[FlatVE] = {
    case ENull => (s: EnvOnlyAbstraction[FlatVE]) => (FlatVEBuilder.simple(Null), s)
  }

  override def pe_eabsent: EAbsent.type => Result[FlatVE] = {
    case EAbsent => (s: EnvOnlyAbstraction[FlatVE]) => (FlatVEBuilder.simple(Absent), s)
  }

  override def pe_eref: ERef => Result[FlatVE] = {

    case ERef(ref) => (s: EnvOnlyAbstraction[FlatVE]) => {
      (ref match {
        case RefId(id) => s.getVar(id.name) match {
          case FlatVE(FlatElem((ASTVal(ast: AbsAST), _))) => FlatVEBuilder.top
          case FlatVE(FlatBot) => if (isPermittedGlobal(id)) Initialize.initGlobal.get(id) match {
            case Some(v: Addr) => FlatVEBuilder.top
            case Some(v) => FlatVE(FlatElem(v, ERef(ref)))
            case _ => FlatVEBuilder.top
          }
          else FlatVEBuilder.top
          case v => v
        }
        case RefProp(ref, expr) => FlatVEBuilder.top
      }, s)
    }
  }

  override def pe_euop: EUOp => Result[FlatVE] = {
    case EUOp(uop, expr) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val (v, s2) = pe(expr)(s)
      (FlatVE(v.v.map { case (v, e) => (Interp.interp(uop, v), EUOp(uop, e)) }), s2)
    }
  }

  override def pe_ebop: EBOp => Result[FlatVE] = {
    case EBOp(bop, left, right) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val (lv, s2) = pe(left)(s)
      val (rv, s3) = pe(right)(s2)
      (FlatVE(lv.v match {
        case FlatElem((v1, e1)) => rv.v.map { case (v2, e2) => (Interp.interp(bop, v1, v2), EBOp(bop, e1, e2)) }
        case FlatTop => FlatTop
        case FlatBot => FlatBot
      }), s3)
    }
  }

  override def pe_etypeof: ETypeOf => Result[FlatVE] = {
    case ETypeOf(expr) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val (v, s2) = pe(expr)(s)
      val vs = v.v match {
        case FlatTop => None
        case FlatBot => None
        case FlatElem((NormalComp(value), _)) => Some(value)
        case FlatElem((CompValue(_, _, _), _)) => None
        case FlatElem((pure: PureValue, _)) => Some(pure)
      }
      ((v.v, vs) match {
        case (_, Some(value)) => value match {
          case Const(_) => FlatVEBuilder.simple(Str("Constant"))
          case (addr: Addr) => FlatVEBuilder.top
          case Func(_) => FlatVEBuilder.simple(Str("Function"))
          case Clo(_, _, _, _) => FlatVEBuilder.simple(Str("Closure"))
          case Cont(_, _, _) => FlatVEBuilder.simple(Str("Continuation"))
          case ASTVal(_) => FlatVEBuilder.simple(Str("AST"))
          case Num(_) | INum(_) => FlatVEBuilder.simple(Str("Number"))
          case BigINum(_) => FlatVEBuilder.simple(Str("BigInt"))
          case Str(_) => FlatVEBuilder.simple(Str("String"))
          case Bool(_) => FlatVEBuilder.simple(Str("Boolean"))
          case Undef => FlatVEBuilder.simple(Str("Undefined"))
          case Null => FlatVEBuilder.simple(Str("Null"))
          case Absent => FlatVEBuilder.simple(Str("Absent"))
        }
        case (FlatBot, _) => FlatVEBuilder.bottom
        case (_, _) => FlatVEBuilder.top
      }, s)
    }

  }

  override def pe_eisinstanceof: EIsInstanceOf => Result[FlatVE] = {
    case EIsInstanceOf(base, name) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val (v, s2) = pe(base)(s)
      (v match {
        case FlatVE(FlatElem((v, e))) => if (v.isAbruptCompletion) FlatVEBuilder.simple(Bool(false))
        else v.escaped match {
          case ASTVal(ast) => FlatVEBuilder.simple(Bool(ast.name == name || ast.getKinds.contains(name)))
          case Str(str) => FlatVEBuilder.simple(Bool(str == name))
          case addr: Addr => FlatVEBuilder.top
          case _ => FlatVEBuilder.simple(Bool(false))
        }
        case FlatVE(FlatBot) => FlatVEBuilder.bottom
        case FlatVE(FlatTop) => FlatVEBuilder.top
      }, s2)
    }
  }

  override def pe_ereturnifabrupt: EReturnIfAbrupt => Result[FlatVE] = {
    case EReturnIfAbrupt(expr, check) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val (nv, s2) = pe(expr)(s)
      (nv match {
        case FlatVE(FlatElem((NormalComp(v), e))) => FlatVE(FlatElem((v, EReturnIfAbrupt(e, check))))
        case FlatVE(FlatElem((v: PureValue, e))) => FlatVE(FlatElem(v, EReturnIfAbrupt(e, check)))
        case FlatVE(FlatBot) => FlatVEBuilder.bottom
        case _ => FlatVEBuilder.top
      }, s2)
    }
  }

}