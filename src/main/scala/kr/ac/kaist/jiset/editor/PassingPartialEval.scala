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

class PassingPartialEval extends PartialEval[EnvOnlyAbstraction[FlatVE]] {
  val vtbuilder: AbstractValueBuilder[FlatVE] = FlatVEBuilder
  val asbuilder: AbstractStateBuilder[EnvOnlyAbstraction[FlatVE]] = EnvOnlyAbstractionBuilder[FlatVE]()(vtbuilder)
  val instTransformer: InstTransformer[FlatVE, EnvOnlyAbstraction[FlatVE]] = new PassingInstTransformer(ExprTransformer(new TopExprEvaluator))
}

class PassingInstTransformer(val pet: ExprTransformer[EnvOnlyAbstraction[FlatVE]]) extends InstTransformer[FlatVE, EnvOnlyAbstraction[FlatVE]] {
  //val pet = ExprTransformer(TopExprEvaluator)
  val vtbuilder: AbstractValueBuilder[FlatVE] = FlatVEBuilder
  val asbuilder: AbstractStateBuilder[EnvOnlyAbstraction[FlatVE]] = EnvOnlyAbstractionBuilder[FlatVE]()(vtbuilder)

  def pe_iseq: ISeq => Result[Inst] = {
    case ISeq(insts) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val (safter, ls) = insts.foldLeft((s, List[Inst]())) {
        case ((s, l), inst) => {
          if (s.mayContinue) {
            val (i2, s2) = pe(inst)(s)
            (s2, l :+ i2)
          } else (s, l)
        }
      }
      (ISeq(ls), safter)
    }
  }
  def pe_iaccess: IAccess => Result[Inst] = {
    case IAccess(id, bexpr, expr, args) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val ((_, baseE), s2) = pet.pe(bexpr)(s)
      val ((_, propE), s3) = pet.pe(expr)(s2)
      val (s4, argsE) = args.foldLeft((s3, List[Expr]())) {
        case ((s, l), expr) => {
          val ((_, e2), s2) = pet.pe(expr)(s)
          (s2, l :+ e2)
        }
      }
      val s5 = s4.setVar(id.name, FlatVEBuilder.top)
      (IAccess(id, baseE, propE, argsE), s5)
    }
  }

  def pe_iapp: IApp => Result[Inst] = {
    case IApp(id, fexpr, args) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val ((_, fexprE), s2) = pet.pe(fexpr)(s)
      val (s3, argsE) = args.foldLeft((s2, List[Expr]())) {
        case ((s, l), expr) => {
          val ((_, e2), s2) = pet.pe(expr)(s)
          (s2, l :+ e2)
        }
      }
      val s4 = s3.setVar(id.name, FlatVEBuilder.top)
      (IApp(id, fexprE, argsE), s4)
    }
  }

  def pe_iappend: IAppend => Result[Inst] = {
    case IAppend(expr, list) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val ((_, expre), s2) = pet.pe(expr)(s)
      val ((_, liste), s3) = pet.pe(list)(s2)
      (IAppend(expre, liste), s3)
    }
  }

  def pe_iassert: IAssert => Result[Inst] = {
    case IAssert(expr) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val ((_, expre), s2) = pet.pe(expr)(s)
      (IAssert(expre), s2)
    }
  }

  def pe_iassign: IAssign => Result[Inst] = {
    case IAssign(RefId(id), expr) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val ((v, e), s2) = pet.pe(expr)(s)
      val s3 = s2.setVar(id.name, v)
      (IAssign(RefId(id), e), s3)
    }
    case IAssign(ref, expr) => (s: EnvOnlyAbstraction[FlatVE]) => {
      def aux(r: Ref, s: EnvOnlyAbstraction[FlatVE]): (Ref, EnvOnlyAbstraction[FlatVE]) = r match {
        case RefId(id) => (RefId(id), s)
        case RefProp(ref, expr) => {
          val (r2, s2) = aux(ref, s)
          val ((_, e2), s3) = pet.pe(expr)(s2)
          (RefProp(ref, e2), s3)
        }
      }
      val (refr, s2) = aux(ref, s)
      val ((_, expre), s3) = pet.pe(expr)(s2)
      (IAssign(refr, expre), s)
    }
  }

  def pe_iclo: IClo => Result[Inst] = {
    case IClo(id, params, captured, body) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val s2 = s.setVar(id.name, FlatVEBuilder.top)
      (IClo(id, params, captured, body), s2)
    }
  }

  def pe_icont: ICont => Result[Inst] = {
    case ICont(id, params, body) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val (nbody, _) = pe(body)(params.foldLeft(s) {
        case (nc, id) => nc.setVar(id.name, FlatVEBuilder.top)
      })
      (ICont(id, params, nbody), s.setVar(id.name, FlatVEBuilder.top))
    }
  }

  def pe_idelete: IDelete => Result[Inst] = {
    case IDelete(ref) => (s: EnvOnlyAbstraction[FlatVE]) => {
      def aux(r: Ref, s: EnvOnlyAbstraction[FlatVE]): (Ref, EnvOnlyAbstraction[FlatVE]) = r match {
        case RefId(id) => (RefId(id), s)
        case RefProp(ref, expr) => {
          val (r2, s2) = aux(ref, s)
          val ((_, e2), s3) = pet.pe(expr)(s2)
          (RefProp(ref, e2), s3)
        }
      }
      val (refr, s2) = aux(ref, s)
      (IDelete(refr), s2)
    }
  }

  def pe_iexpr: IExpr => Result[Inst] = {
    case IExpr(expr) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val ((_, e), s2) = pet.pe(expr)(s)
      (IExpr(e), s2)
    }
  }

  def pe_iif: IIf => Result[Inst] = {
    case IIf(cond, thenInst, elseInst) => (dcontext: EnvOnlyAbstraction[FlatVE]) => {
      val ((_, ce), dcontext1) = pet.pe(cond)(dcontext)
      val (thenInstI, dcontext2) = pe(thenInst)(dcontext1)
      val (elseInstI, dcontext3) = pe(elseInst)(dcontext1)
      val nstate = dcontext2 join dcontext3

      (IIf(
        ce,
        thenInstI,
        elseInstI
      ), nstate)
    }
  }

  def pe_ilet: ILet => Result[Inst] = {
    case ILet(id, expr) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val ((v, e), s2) = pet.pe(expr)(s)
      val s3 = s2.setVar(id.name, v)
      (ILet(id, e), s3)
    }
  }

  def pe_iprepend: IPrepend => Result[Inst] = {
    case IPrepend(expr, list) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val ((_, expre), s2) = pet.pe(expr)(s)
      val ((_, liste), s3) = pet.pe(list)(s2)
      (IPrepend(expre, liste), s3)
    }
  }

  def pe_iprint: IPrint => Result[Inst] = {
    case IPrint(expr) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val ((_, expre), s2) = pet.pe(expr)(s)
      (IPrint(expre), s2)
    }
  }

  def pe_ireturn: IReturn => Result[Inst] = {
    case IReturn(expr) => (s: EnvOnlyAbstraction[FlatVE]) => {
      val ((v, ne), s2) = pet.pe(expr)(s)
      (IReturn(ne), s2.setRet(v))
    }
  }

  def pe_ithrow: IThrow => Result[Inst] = {
    case IThrow(name) => (s: EnvOnlyAbstraction[FlatVE]) => (IThrow(name), s)
  }

  def pe_iwhile: IWhile => Result[Inst] = {
    case IWhile(cond, body) => (s: EnvOnlyAbstraction[FlatVE]) =>
      (IWhile(cond, body), asbuilder.top)
  }

  def pe_iwithcont: IWithCont => Result[Inst] = {
    case IWithCont(id, params, body) => (s: EnvOnlyAbstraction[FlatVE]) =>
      val (nbody, _) = pe(body)(s.setVar(id.name, FlatVEBuilder.top))
      (IWithCont(id, params, nbody), params.foldLeft(s) {
        case (nc, id) => nc.setVar(id.name, FlatVEBuilder.top)
      })
  }

}

class TopExprEvaluator extends ExprEvaluator[FlatVE, EnvOnlyAbstraction[FlatVE]] {
  val vtbuilder = FlatVEBuilder
  val asbuilder = EnvOnlyAbstractionBuilder[FlatVE]()(vtbuilder)

  def pe_enum: ENum => Result[FlatVE] = {
    case ENum(_) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_einum: EINum => Result[FlatVE] = {
    case EINum(n) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_ebiginum: EBigINum => Result[FlatVE] = {
    case EBigINum(b) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_estr: EStr => Result[FlatVE] = {
    case EStr(str) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_ebool: EBool => Result[FlatVE] = {
    case EBool(b) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_eundef: EUndef.type => Result[FlatVE] = {
    case EUndef => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_enull: ENull.type => Result[FlatVE] = {
    case ENull => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_eabsent: EAbsent.type => Result[FlatVE] = {
    case EAbsent => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_econst: EConst => Result[FlatVE] = {
    case EConst(name) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_ecomp: EComp => Result[FlatVE] = {
    case EComp(ty, value, target) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_emap: EMap => Result[FlatVE] = {
    case EMap(ty, props) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_elist: EList => Result[FlatVE] = {
    case EList(exprs) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_esymbol: ESymbol => Result[FlatVE] = {
    case ESymbol(desc) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_epop: EPop => Result[FlatVE] = {
    case EPop(list, idx) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_eref: ERef => Result[FlatVE] = {
    case ERef(ref) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_euop: EUOp => Result[FlatVE] = {
    case EUOp(uop, expr) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_ebop: EBOp => Result[FlatVE] = {
    case EBOp(bop, left, right) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_etypeof: ETypeOf => Result[FlatVE] = {
    case ETypeOf(expr) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)

  }

  def pe_eiscompletion: EIsCompletion => Result[FlatVE] = {
    case EIsCompletion(expr) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_eisinstanceof: EIsInstanceOf => Result[FlatVE] = {
    case EIsInstanceOf(base, name) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_egetelems: EGetElems => Result[FlatVE] = {
    case EGetElems(base, name) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)

  }

  def pe_egetsyntax: EGetSyntax => Result[FlatVE] = {
    case EGetSyntax(base) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)

  }

  def pe_eparsesyntax: EParseSyntax => Result[FlatVE] = {
    case EParseSyntax(code, rule, parserParams) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)

  }

  def pe_econvert: EConvert => Result[FlatVE] = {
    case EConvert(source, target, flags) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)

  }

  def pe_econtains: EContains => Result[FlatVE] = {
    case EContains(list, elem) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)

  }

  def pe_ereturnifabrupt: EReturnIfAbrupt => Result[FlatVE] = {
    case EReturnIfAbrupt(expr, check) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }
  def pe_ecopy: ECopy => Result[FlatVE] = {
    case ECopy(obj) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)

  }

  def pe_ekeys: EKeys => Result[FlatVE] = {
    case EKeys(mobj, intSorted) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_enotsupported: ENotSupported => Result[FlatVE] = {
    case ENotSupported(msg) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }

  def pe_refid: RefId => Result[FlatVE] = {
    case RefId(id) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }
  def pe_refprop: RefProp => Result[FlatVE] = {
    case RefProp(ref, expr) => (s: EnvOnlyAbstraction[FlatVE]) => (vtbuilder.top, s)
  }
}