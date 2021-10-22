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

class PassingPartialEval extends PartialEval {
  import PartialStateMonad._

  def pe_iseq: ISeq => Result[Inst] = {
    case ISeq(insts) => PartialStateMonad.join(insts.map(pe)).map(ISeq)
  }
  def pe_iaccess: IAccess => Result[Inst] = {
    case IAccess(id, bexpr, expr, args) => for {
      baseE <- pe(bexpr)
      propE <- pe(expr)
      argsE <- PartialStateMonad.join(args.map(pe))
      _ <- (context: S) => context.labelwiseContext.setId(id.name, PartialExpr.mkDynamic(ERef(RefId(id))))
    } yield IAccess(id, baseE.expr, propE.expr, argsE.map(_.expr))
  }
  def pe_iapp: IApp => Result[Inst] = {
    case IApp(id, fexpr, args) => for {
      fexpr <- pe(fexpr)
      argsE <- PartialStateMonad.join(args.map(pe))
      _ <- (context: S) => context.updateLabelwise((u) => u.setId(id.name, PartialExpr.mkDynamic(ERef(RefId(id)))))
    } yield IApp(id, fexpr.expr, argsE.map(_.expr))
  }
  def pe_iappend: IAppend => Result[Inst] = {
    case IAppend(expr, list) => for {
      expre <- pe(expr)
      liste <- pe(list)
    } yield IAppend(expre.expr, liste.expr)
  }
  def pe_iassert: IAssert => Result[Inst] = {
    case IAssert(expr) => for {
      expre <- pe(expr)
    } yield IAssert(expre.expr)
  }
  def pe_iassign: IAssign => Result[Inst] = {
    case IAssign(ref, expr) => for {
      refr <- pe(ref)
      expre <- pe(expr)
    } yield { IAssign(refr, expre.expr) } // TODO
  }
  def pe_iclo: IClo => Result[Inst] = {
    case IClo(id, params, captured, body) => for {
      _ <- (context: S) => context.updateLabelwise((u) => u.setId(id.name, PartialExpr.mkDynamic(ERef(RefId(id)))))
    } yield IClo(id, params, captured, body)
  }
  def pe_icont: ICont => Result[Inst] = {
    case ICont(id, params, body) => (context: S) => {
      val (nbody, _) = pe(body)(params.foldLeft(context) {
        case (nc, id) => nc.updateLabelwise((u) => u.setId(id.name, PartialExpr.mkDynamic(ERef(RefId(id)))))
      })
      (ICont(id, params, nbody), context.updateLabelwise((u) => u.setId(id.name, PartialExpr.mkDynamic(ERef(RefId(id))))))
    }
  }
  def pe_idelete: IDelete => Result[Inst] = {
    case IDelete(ref) => for {
      refr <- pe(ref)
    } yield IDelete(refr)
  }
  def pe_iexpr: IExpr => Result[Inst] = {
    case IExpr(expr) => for {
      e <- pe(expr)
    } yield IExpr(e.expr)
  }

  def pe_iif: IIf => Result[Inst] = {
    case IIf(cond, thenInst, elseInst) => (dcontext: S) => {
      val (c, dcontext1) = pe(cond)(dcontext)
      val (thenInstI, dcontext2) = pe(thenInst)(dcontext1)
      val (elseInstI, dcontext3) = pe(elseInst)(PartialContextImpl(dcontext1.labelwiseContext, dcontext2.globalContext))
      val ((eif, efalse), nstate) = dcontext2.labelwiseContext merge dcontext3.labelwiseContext

      (IIf(
        c.expr,
        ISeq(eif.map { case (x, v) => ILet(Id(x), v.expr) } :+ thenInstI),
        ISeq(efalse.map { case (x, v) => ILet(Id(x), v.expr) } :+ elseInstI)
      ), PartialContextImpl(nstate, dcontext3.globalContext))
    }
  }

  def pe_ilet: ILet => Result[Inst] = {
    case ILet(id, expr) => for {
      v <- pe(expr)
      _ <- (context: S) => context.updateLabelwise((u) => u.setId(id.name, PartialExpr.mkDynamic(ERef(RefId(id)))))
    } yield ILet(id, v.expr)
  }

  def pe_iprepend: IPrepend => Result[Inst] = {
    case IPrepend(expr, list) => for {
      expre <- pe(expr)
      liste <- pe(list)
    } yield IPrepend(expre.expr, liste.expr)
  }

  def pe_iprint: IPrint => Result[Inst] = {
    case IPrint(expr) => for {
      expre <- pe(expr)
    } yield IPrint(expre.expr)
  }

  def pe_ireturn: IReturn => Result[Inst] = {
    case IReturn(expr) => for {
      ne <- pe(expr)
      _ <- (context: S) => context.updateLabelwise((u) => u.setRet(Some(ne)))
    } yield IReturn(ne.expr)

  }

  def pe_ithrow: IThrow => Result[Inst] = {
    case IThrow(name) => IThrow(name)
  }

  def pe_iwhile: IWhile => Result[Inst] = {
    case IWhile(cond, body) => for {
      _ <- (context: S) => context.toDynamic
    } yield IWhile(cond, body) // TODO
  }

  def pe_iwithcont: IWithCont => Result[Inst] = {
    case IWithCont(id, params, body) => (context: S) => {
      val (nbody, _) = pe(body)(context.updateLabelwise((u) => u.setId(id.name, PartialExpr.mkDynamic(ERef(RefId(id))))))
      (IWithCont(id, params, nbody), params.foldLeft(context) {
        case (nc, id) => nc.updateLabelwise((u) => u.setId(id.name, PartialExpr.mkDynamic(ERef(RefId(id)))))
      })
    }
  }

  def pe_enum: ENum => Result[PartialValue] = {
    case ENum(n) => PartialExpr.mkDynamic(ENum(n))
  }

  def pe_einum: EINum => Result[PartialValue] = {
    case EINum(n) => PartialExpr.mkDynamic(EINum(n))
  }

  def pe_ebiginum: EBigINum => Result[PartialValue] = {
    case EBigINum(b) => PartialExpr.mkDynamic(EBigINum(b))
  }

  def pe_estr: EStr => Result[PartialValue] = {
    case EStr(str) => PartialExpr.mkDynamic(EStr(str))
  }

  def pe_ebool: EBool => Result[PartialValue] = {
    case EBool(b) => PartialExpr.mkDynamic(EBool(b))
  }

  def pe_eundef: EUndef.type => Result[PartialValue] = {
    case EUndef => PartialExpr.mkDynamic(EUndef)
  }

  def pe_enull: ENull.type => Result[PartialValue] = {
    case ENull => PartialExpr.mkDynamic(ENull)
  }

  def pe_eabsent: EAbsent.type => Result[PartialValue] = {
    case EAbsent => PartialExpr.mkDynamic(EAbsent)
  }

  def pe_econst: EConst => Result[PartialValue] = {
    case EConst(name) => PartialExpr.mkDynamic(EConst(name))
  }

  def pe_ecomp: EComp => Result[PartialValue] = {
    case EComp(ty, value, target) => for {
      tye <- pe(ty)
      ve <- pe(value)
      targete <- pe(target)
    } yield PartialExpr.mkDynamic(EComp(tye.expr, ve.expr, targete.expr))
  }

  def pe_emap: EMap => Result[PartialValue] = {
    case EMap(ty, props) => for {
      propse <- PartialStateMonad.join(props.map { case (ek, ev) => pe(ek).flatMap((vk) => pe(ev).flatMap((vv) => (vk, vv))) })
    } yield PartialExpr.mkDynamic(EMap(ty, propse.map { case (ek, ev) => (ek.expr, ev.expr) }))
  }

  def pe_elist: EList => Result[PartialValue] = {
    case EList(exprs) => for {
      le <- PartialStateMonad.join(exprs.map(pe))
    } yield (PartialExpr.mkDynamic(EList(le.map(_.expr))))
  }

  def pe_esymbol: ESymbol => Result[PartialValue] = {
    case ESymbol(desc) => for {
      desce <- pe(desc)
    } yield (PartialExpr.mkDynamic(ESymbol(desce.expr)))
  }

  def pe_epop: EPop => Result[PartialValue] = {
    case EPop(list, idx) => for {
      liste <- pe(list)
      idxe <- pe(idx)
    } yield (PartialExpr.mkDynamic(EPop(liste.expr, idxe.expr)))
  }

  def pe_eref: ERef => Result[PartialValue] = {
    case ERef(ref) => for {
      refr <- pe(ref)
    } yield PartialExpr.mkDynamic(ERef(refr))
  }

  def pe_euop: EUOp => Result[PartialValue] = {
    case EUOp(uop, expr) => for {
      e <- pe(expr)
    } yield PartialExpr.mkDynamic(EUOp(uop, e.expr))
  }

  def pe_ebop: EBOp => Result[PartialValue] = {
    case EBOp(bop, left, right) => for {
      le <- pe(left)
      re <- pe(right)
    } yield PartialExpr.mkDynamic(EBOp(bop, le.expr, re.expr))
  }

  def pe_etypeof: ETypeOf => Result[PartialValue] = {
    case ETypeOf(expr) => for {
      e <- pe(expr)
    } yield PartialExpr.mkDynamic(ETypeOf(e.expr))

  }

  def pe_eiscompletion: EIsCompletion => Result[PartialValue] = {
    case EIsCompletion(expr) => for {
      e <- pe(expr)
    } yield PartialExpr.mkDynamic(EIsCompletion(e.expr))
  }

  def pe_eisinstanceof: EIsInstanceOf => Result[PartialValue] = {
    case EIsInstanceOf(base, name) => for {
      be <- pe(base)
    } yield PartialExpr.mkDynamic(EIsInstanceOf(be.expr, name))
  }

  def pe_egetelems: EGetElems => Result[PartialValue] = {
    case EGetElems(base, name) => for {
      basee <- pe(base)
    } yield PartialExpr.mkDynamic(EGetElems(basee.expr, name))

  }

  def pe_egetsyntax: EGetSyntax => Result[PartialValue] = {
    case EGetSyntax(base) => for {
      basee <- pe(base)
    } yield PartialExpr.mkDynamic(EGetSyntax(basee.expr))

  }

  def pe_eparsesyntax: EParseSyntax => Result[PartialValue] = {
    case EParseSyntax(code, rule, parserParams) => for {
      codee <- pe(code)
      rulee <- pe(rule)
    } yield PartialExpr.mkDynamic(EParseSyntax(codee.expr, rulee.expr, parserParams))

  }

  def pe_econvert: EConvert => Result[PartialValue] = {
    case EConvert(source, target, flags) => for {
      sourcee <- pe(source)
      flagse <- PartialStateMonad.join(flags.map(pe))
    } yield PartialExpr.mkDynamic(EConvert(sourcee.expr, target, flagse.map(_.expr)))

  }

  def pe_econtains: EContains => Result[PartialValue] = {
    case EContains(list, elem) => for {
      liste <- pe(list)
      eleme <- pe(elem)
    } yield PartialExpr.mkDynamic(EContains(liste.expr, eleme.expr))

  }

  def pe_ereturnifabrupt: EReturnIfAbrupt => Result[PartialValue] = {
    case EReturnIfAbrupt(expr, check) => for {
      ne <- pe(expr)
    } yield PartialExpr.mkDynamic(EReturnIfAbrupt(ne.expr, check))
  }
  def pe_ecopy: ECopy => Result[PartialValue] = {
    case ECopy(obj) => for {
      obje <- pe(obj)
    } yield PartialExpr.mkDynamic(ECopy(obje.expr))

  }

  def pe_ekeys: EKeys => Result[PartialValue] = {
    case EKeys(mobj, intSorted) => for {
      mobje <- pe(mobj)
    } yield PartialExpr.mkDynamic(EKeys(mobje.expr, intSorted))
  }

  def pe_enotsupported: ENotSupported => Result[PartialValue] = {
    case ENotSupported(msg) => PartialExpr.mkDynamic(ENotSupported(msg))
  }

  def pe_refid: RefId => Result[Ref] = {
    case RefId(id) => RefId(id)
  }
  def pe_refprop: RefProp => Result[Ref] = {
    case RefProp(ref, expr) => for {
      refr <- pe(ref)
      expre <- pe(expr)
    } yield RefProp(refr, expre.expr)
  }
}
